package com.easycloud.common;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Set;


/**
 * API 签名工具类 - 精确复刻 PHP Arr_sign()
 */
public class ApiSignature {

    /**
     * 需要排除的 key - 对应 PHP 中 unset 的字段
     */
    private static final Set<String> EXCLUDED_KEYS = Set.of(
            "sign", "app", "api", "value", "PHPSESSID", "sec_defend", "sidenav-state"
    );

    /**
     * 生成签名 - 对应 PHP Arr_sign($arr, $key, true)
     *
     * 逻辑:
     * 1. 排除 sign/app/api/value/PHPSESSID/sec_defend/sidenav-state
     * 2. 按原始顺序拼接 k=v& (使用 LinkedHashMap 保持顺序)
     * 3. 末尾追加 appkey
     * 4. 计算 MD5 (小写)
     *
     * @param params  参数 Map
     * @param appkey  应用密钥
     * @return MD5 签名(小写32位)
     */
    public static String sign(Map<String, String> params, String appkey) {
        return sign(params, appkey, true);
    }

    /**
     * 生成签名 - 对应 PHP Arr_sign($arr, $key, $md5)
     *
     * @param params 参数 Map
     * @param appkey 应用密钥
     * @param md5    是否返回 MD5，false 则返回原始签名字符串
     * @return MD5 签名或原始签名字符串
     */
    public static String sign(Map<String, String> params, String appkey, boolean md5) {
        StringBuilder sb = new StringBuilder();

        // 按顺序遍历参数，排除指定 key
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            if (EXCLUDED_KEYS.contains(key)) {
                continue;
            }
            sb.append(key).append('=').append(entry.getValue()).append('&');
        }

        // 末尾追加 appkey
        sb.append(appkey);

        String signStr = sb.toString();

        if (md5) {
            return md5(signStr);
        } else {
            return signStr;
        }
    }

    /**
     * 验证签名
     *
     * @param params 参数 Map
     * @param sign   待验证的签名
     * @param appkey 应用密钥
     * @return 签名是否匹配
     */
    public static boolean verify(Map<String, String> params, String sign, String appkey) {
        if (sign == null || sign.isEmpty()) {
            return false;
        }
        String computed = sign(params, appkey, true);
        return computed.equalsIgnoreCase(sign);
    }

    /**
     * 生成响应校验码 - 对应 PHP 中 md5($time . $appkey . $value)
     *
     * @param time    时间戳
     * @param appkey  应用密钥
     * @param value   响应值
     * @return MD5 校验码(小写)
     */
    public static String responseCheck(long time, String appkey, String value) {
        String raw = time + appkey + (value != null ? value : "");
        return md5(raw);
    }

    /**
     * MD5 摘要 - 小写32位
     *
     * @param input 输入字符串
     * @return MD5 摘要(小写)
     */
    private static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(32);
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xFF));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // MD5 is always available in Java
            throw new RuntimeException("MD5 not available", e);
        }
    }
}
