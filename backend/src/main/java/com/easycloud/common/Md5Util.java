package com.easycloud.common;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * MD5 工具 - 兼容原 PHP 系统的密码加密: md5(pwd + '!@#%!s!0')
 */
public class Md5Util {

    /** 密码盐值 - 对应 PHP $password_hash */
    private static final String PASSWORD_SALT = "!@#%!s!0";

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

    public static String encryptPassword(String rawPassword) {
        return md5(rawPassword + PASSWORD_SALT);
    }

    /**
     * 校验密码：兼容 PHP 明文存储与 Java MD5 加盐存储
     */
    public static boolean verifyPassword(String rawPassword, String stored) {
        if (rawPassword == null || stored == null) {
            return false;
        }
        return stored.equals(rawPassword) || stored.equals(encryptPassword(rawPassword));
    }
}
