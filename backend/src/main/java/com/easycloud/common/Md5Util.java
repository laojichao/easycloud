package com.easycloud.common;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * MD5 摘要工具类
 * <p>
 * 提供 MD5 哈希计算和密码加盐加密功能，精确复刻原 PHP 项目的加密逻辑。
 * <ul>
 *   <li>md5(): 通用 MD5 摘要计算，对应 PHP md5() 函数</li>
 *   <li>encryptPassword(): 使用固定盐值对密码进行加盐 MD5，对应 PHP md5($pwd . '!@#%!s!0')</li>
 *   <li>verifyPassword(): 验证明文密码是否与存储的加密密码匹配</li>
 * </ul>
 * <p>
 * 密码盐值 '!@#%!s!0' 与原 PHP 项目保持一致，确保已有用户密码可正常验证。
 *
 * @author EasyCloud
 * @since 1.0.0
 */
public class Md5Util {

    /** 密码盐值 - 对应 PHP $password_hash = '!@#%!s!0' */
    private static final String PASSWORD_SALT = "!@#%!s!0";

    /**
     * 计算 MD5 摘要
     *
     * @param input 输入字符串
     * @return 32位小写十六进制 MD5 值
     */
    public static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(32);
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xFF));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("MD5 not available", e);
        }
    }

    /**
     * 使用固定盐值加密密码（对应 PHP md5(pwd + '!@#%!s!0')）
     *
     * @param password 原始密码
     * @return 加密后的密码
     */
    public static String encryptPassword(String password) {
        return md5(password + PASSWORD_SALT);
    }

    /**
     * 验证密码
     *
     * @param password     原始密码
     * @param storedPwd    存储的加密密码
     * @return 是否匹配
     */
    public static boolean verifyPassword(String password, String storedPwd) {
        return encryptPassword(password).equals(storedPwd);
    }
}
