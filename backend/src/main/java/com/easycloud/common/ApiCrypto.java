package com.easycloud.common;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * API 加解密工具类 - 精确复刻 PHP 加密逻辑
 * 支持5种加密类型: 0=明文, 1=RC4(GBK), 2=Base64, 3=RC4(hex), 4=AES, 5=Base64v2
 */
public class ApiCrypto {

    private static final Charset GBK = Charset.forName("GBK");

    /**
     * UTF-8 转 GBK 编码 - 对应 PHP mi_rc4_encode()
     * PHP mi_rc4_encode 使用 iconv('UTF-8', 'GBK//IGNORE', $str)
     *
     * @param str UTF-8 字符串
     * @return GBK 编码后的字节数组
     */
    private static byte[] toGbkBytes(String str) {
        if (str == null) {
            return new byte[0];
        }
        return str.getBytes(GBK);
    }

    /**
     * GBK 字节数组转回 UTF-8 字符串
     *
     * @param bytes GBK 编码的字节数组
     * @return UTF-8 字符串
     */
    private static String fromGbkBytes(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }
        return new String(bytes, GBK);
    }

    /**
     * 自定义RC4加密（GBK编码）- 对应 PHP mi_rc4($data, $pwd, 0)
     * 流程: UTF-8->GBK转码 -> RC4 -> 输出hex
     *
     * @param data 明文数据(UTF-8)
     * @param pwd  密钥(UTF-8)
     * @return hex编码的密文
     */
    public static String miRc4Encrypt(String data, String pwd) {
        byte[] dataBytes = toGbkBytes(data);
        byte[] pwdBytes = toGbkBytes(pwd);
        byte[] cipher = rc4Internal(dataBytes, pwdBytes);
        return bytesToHex(cipher);
    }

    /**
     * 自定义RC4解密（GBK编码）- 对应 PHP mi_rc4($data, $pwd, 1)
     * 流程: hex解码 -> RC4 -> GBK->UTF-8转码
     *
     * @param hexData hex编码的密文
     * @param pwd     密钥(UTF-8)
     * @return 明文数据(UTF-8)
     */
    public static String miRc4Decrypt(String hexData, String pwd) {
        byte[] dataBytes = hexToBytes(hexData);
        byte[] pwdBytes = toGbkBytes(pwd);
        byte[] plain = rc4Internal(dataBytes, pwdBytes);
        return fromGbkBytes(plain);
    }

    /**
     * 标准RC4加解密 - 对应 PHP rc4()
     * 不做任何编码转换，直接对原始字节操作
     *
     * @param data 原始数据字节
     * @param pwd  密钥字节
     * @return 处理后的字节
     */
    public static byte[] rc4(byte[] data, byte[] pwd) {
        return rc4Internal(data, pwd);
    }

    /**
     * RC4 核心算法实现 - 精确复刻 PHP rc4() / mi_rc4() 的 RC4 部分
     * PHP 实现特点: 初始 a=0，循环内先 a=(a+1)%256 再使用 box[a]
     * 这与标准 RC4 (初始 a=0,j=0, a先自增) 完全一致
     *
     * @param data 数据字节
     * @param pwd  密钥字节
     * @return RC4 处理后的字节
     */
    private static byte[] rc4Internal(byte[] data, byte[] pwd) {
        if (data == null || data.length == 0) {
            return new byte[0];
        }
        if (pwd == null || pwd.length == 0) {
            return data.clone();
        }

        int pwdLength = pwd.length;
        int dataLength = data.length;

        // KSA (Key Scheduling Algorithm)
        int[] box = new int[256];
        int[] key = new int[256];
        for (int i = 0; i < 256; i++) {
            key[i] = pwd[i % pwdLength] & 0xFF;
            box[i] = i;
        }

        int j = 0;
        for (int i = 0; i < 256; i++) {
            j = (j + box[i] + key[i]) % 256;
            int tmp = box[i];
            box[i] = box[j];
            box[j] = tmp;
        }

        // PRGA (Pseudo-Random Generation Algorithm)
        byte[] cipher = new byte[dataLength];
        int a = 0;
        j = 0;
        for (int i = 0; i < dataLength; i++) {
            a = (a + 1) % 256;
            j = (j + box[a]) % 256;
            int tmp = box[a];
            box[a] = box[j];
            box[j] = tmp;
            int k = box[(box[a] + box[j]) % 256];
            cipher[i] = (byte) ((data[i] & 0xFF) ^ k);
        }

        return cipher;
    }

    /**
     * AES-128-CBC 加密 - 对应 PHP AES()
     * Key: 'y' + rc4Key.substring(0, 13) + 'gg' (正好16字节)
     * IV: "0102030405060708" (16字节 ASCII 字符串)
     * Padding: PKCS7
     * 输出: hex 编码
     *
     * @param data   明文(UTF-8)
     * @param rc4Key 应用的 rc4_key
     * @return hex 编码的密文
     */
    public static String aesEncrypt(String data, String rc4Key) {
        try {
            // 构造 AES Key: 'y' + rc4_key前13字符 + 'gg' = 16字节
            // PHP: 'y'.substr($mi['rc4_key'],0,13).'gg' -- substr 超出长度时返回全部
            String rc4Part = rc4Key.length() >= 13 ? rc4Key.substring(0, 13) : rc4Key;
            String keyStr = "y" + rc4Part + "gg";
            byte[] keyBytes = keyStr.getBytes(StandardCharsets.US_ASCII);

            // IV: "0102030405060708" 是16个ASCII字符 = 16字节
            byte[] ivBytes = "0102030405060708".getBytes(StandardCharsets.US_ASCII);

            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));

            return bytesToHex(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("AES encryption failed", e);
        }
    }

    /**
     * AES-128-CBC 解密
     *
     * @param hexData hex 编码的密文
     * @param rc4Key  应用的 rc4_key
     * @return 明文(UTF-8)
     */
    public static String aesDecrypt(String hexData, String rc4Key) {
        try {
            String rc4Part = rc4Key.length() >= 13 ? rc4Key.substring(0, 13) : rc4Key;
            String keyStr = "y" + rc4Part + "gg";
            byte[] keyBytes = keyStr.getBytes(StandardCharsets.US_ASCII);

            byte[] ivBytes = "0102030405060708".getBytes(StandardCharsets.US_ASCII);

            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            byte[] decrypted = cipher.doFinal(hexToBytes(hexData));

            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("AES decryption failed", e);
        }
    }

    /**
     * 根据加密类型解密数据
     *
     * @param data       加密的数据
     * @param miType     加密类型(0-4): 0=明文, 1=RC4(GBK), 2=Base64, 3=RC4(原始), 4=RSA
     * @param rc4Key     密钥(RC4/AES用)
     * @param privateKey RSA私钥(miType=4用)
     * @return 解密后的 key=value& 格式字符串
     */
    public static String decrypt(String data, int miType, String rc4Key, String privateKey) {
        if (data == null || data.isEmpty()) {
            return "";
        }
        switch (miType) {
            case 0:
                // 明文，直接返回
                return data;
            case 1:
                // RC4(GBK编码) 加密，hex输出 -> 解密为 key=value& 格式
                return miRc4Decrypt(data, rc4Key);
            case 2:
                // Base64 解码
                return new String(java.util.Base64.getDecoder().decode(data), StandardCharsets.UTF_8);
            case 3:
                // RC4(hex) - 使用标准RC4，不做GBK编码转换
                byte[] rawCipher = hexToBytes(data);
                byte[] rawPlain = rc4(rawCipher, rc4Key.getBytes(StandardCharsets.UTF_8));
                return new String(rawPlain, StandardCharsets.UTF_8);
            case 4:
                // RSA 私钥解密
                return rsaDecrypt(data, privateKey);
            case 5:
                // Base64v2 解码（与 miType=2 相同）
                return new String(java.util.Base64.getDecoder().decode(data), StandardCharsets.UTF_8);
            default:
                throw new IllegalArgumentException("Unknown miType: " + miType);
        }
    }

    /**
     * 兼容旧签名
     */
    public static String decrypt(String data, int miType, String rc4Key) {
        return decrypt(data, miType, rc4Key, null);
    }

    /**
     * 根据加密类型加密响应数据
     * 注意: PHP中 miType=4 输出用AES(非RSA)，输入用RSA
     *
     * @param data   JSON 字符串或明文数据
     * @param miType 加密类型(0-4): 0=明文, 1=RC4(GBK), 2=Base64, 3=RC4(原始), 4=AES
     * @param rc4Key 密钥
     * @return 加密后的字符串
     */
    public static String encrypt(String data, int miType, String rc4Key) {
        if (data == null || data.isEmpty()) {
            return "";
        }
        switch (miType) {
            case 0:
                // 明文
                return data;
            case 1:
                // RC4(GBK编码) -> hex
                return miRc4Encrypt(data, rc4Key);
            case 2:
                // Base64 编码
                return java.util.Base64.getEncoder().encodeToString(data.getBytes(StandardCharsets.UTF_8));
            case 3:
                // RC4(hex) - 使用标准RC4，不做GBK编码转换
                byte[] plain = data.getBytes(StandardCharsets.UTF_8);
                byte[] cipher = rc4(plain, rc4Key.getBytes(StandardCharsets.UTF_8));
                return bytesToHex(cipher);
            case 4:
                // AES-128-CBC (PHP输出侧miType=4用AES，非RSA)
                return aesEncrypt(data, rc4Key);
            case 5:
                // Base64v2 编码（与 miType=2 相同）
                return java.util.Base64.getEncoder().encodeToString(data.getBytes(StandardCharsets.UTF_8));
            default:
                throw new IllegalArgumentException("Unknown miType: " + miType);
        }
    }

    /**
     * RSA 私钥解密 - 对应 PHP RSA_SMI()
     *
     * @param hexData    hex编码的密文
     * @param privateKey PEM格式私钥
     * @return 解密后的明文
     */
    public static String rsaDecrypt(String hexData, String privateKey) {
        if (privateKey == null || privateKey.isEmpty()) {
            throw new RuntimeException("RSA私钥未配置");
        }
        try {
            byte[] encrypted = hexToBytes(hexData);
            // 处理PEM格式私钥
            String cleanKey = privateKey
                    .replace("-----BEGIN RSA PRIVATE KEY-----", "")
                    .replace("-----END RSA PRIVATE KEY-----", "")
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] keyBytes = java.util.Base64.getDecoder().decode(cleanKey);

            java.security.KeyFactory keyFactory = java.security.KeyFactory.getInstance("RSA");
            java.security.spec.PKCS8EncodedKeySpec keySpec = new java.security.spec.PKCS8EncodedKeySpec(keyBytes);
            java.security.PrivateKey key = keyFactory.generatePrivate(keySpec);

            javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(javax.crypto.Cipher.DECRYPT_MODE, key);
            byte[] decrypted = cipher.doFinal(encrypted);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("RSA解密失败", e);
        }
    }

    /**
     * 解析 key=value& 格式字符串为 Map
     * 例如: "name=test&age=18&" -> {name: "test", age: "18"}
     *
     * @param text key=value& 格式字符串
     * @return 参数 Map（保持插入顺序）
     */
    public static Map<String, String> parseKeyValue(String text) {
        Map<String, String> map = new LinkedHashMap<>();
        if (text == null || text.isEmpty()) {
            return map;
        }
        // 去掉末尾的 &
        if (text.endsWith("&")) {
            text = text.substring(0, text.length() - 1);
        }
        String[] pairs = text.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf('=');
            if (idx > 0) {
                String key = pair.substring(0, idx);
                String value = idx < pair.length() - 1 ? pair.substring(idx + 1) : "";
                map.put(key, value);
            }
        }
        return map;
    }

    /**
     * 将 Map 编码为 key=value& 格式字符串
     *
     * @param params 参数 Map
     * @return key=value& 格式字符串
     */
    public static String toKeyValue(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(entry.getKey()).append('=').append(entry.getValue()).append('&');
        }
        return sb.toString();
    }

    // ========== 工具方法 ==========

    /**
     * 字节数组转 hex 字符串（小写）- 对应 PHP bin2hex()
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b & 0xFF));
        }
        return sb.toString();
    }

    /**
     * hex 字符串转字节数组 - 对应 PHP hex2bin()
     */
    private static byte[] hexToBytes(String hex) {
        if (hex == null || hex.isEmpty()) {
            return new byte[0];
        }
        int len = hex.length();
        byte[] bytes = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return bytes;
    }
}
