package com.easycloud.common;

import java.util.regex.Pattern;

/**
 * 输入净化工具 - 精确复刻 PHP purge() / SafeFilter() 函数
 * 用于 API 接口参数清洗，防止 XSS 和注入攻击
 */
public class InputSanitizer {

    /** XSS 标签模式 */
    private static final Pattern XSS_TAG_PATTERN = Pattern.compile(
            "<(/?)(script|iframe|style|html|body|title|link|meta|object|\\?|%)([^>]*?)>",
            Pattern.CASE_INSENSITIVE
    );

    /** 事件处理器模式 */
    private static final Pattern EVENT_HANDLER_PATTERN = Pattern.compile(
            "on[a-zA-Z]+\\s*=([^>]*>)",
            Pattern.CASE_INSENSITIVE
    );

    /** SQL 注入关键词模式 */
    private static final Pattern SQL_PATTERN = Pattern.compile(
            "(?i)(select\\s|insert\\s|and\\s|or\\s|create\\s|update\\s|delete\\s|alter\\s|count\\s|'|/\\*|\\*|\\.\\./|\\./|\\^|union\\s|into\\s|load_file|outfile|dump)",
            Pattern.CASE_INSENSITIVE
    );

    /** 非打印字符 */
    private static final Pattern CONTROL_CHARS = Pattern.compile("[\\x00-\\x08\\x0b-\\x0c\\x0e-\\x19]");

    /**
     * 净化输入字符串 - 对应 PHP purge()
     * 流程: 去除控制字符 → 去除内部空格 → 过滤XSS → 过滤SQL关键字 → HTML实体编码
     *
     * @param input  原始输入
     * @param trim   是否去除内部空格
     * @param filter 是否过滤XSS/SQL关键字
     * @return 净化后的字符串
     */
    public static String purge(String input, boolean trim, boolean filter) {
        if (input == null) return null;

        String result = input;

        // 1. 去除非打印字符
        result = CONTROL_CHARS.matcher(result).replaceAll("");

        // 2. 去除内部空格（PHP: preg_replace('/\s+/','',$string)）
        if (trim) {
            result = result.replaceAll("\\s+", "");
        }

        // 3. 过滤 XSS 和 SQL 关键字
        if (filter) {
            result = XSS_TAG_PATTERN.matcher(result).replaceAll("");
            result = EVENT_HANDLER_PATTERN.matcher(result).replaceAll("");
            result = SQL_PATTERN.matcher(result).replaceAll("");
            // strip_tags + htmlentities
            result = stripTags(result);
            result = htmlEntities(result);
        }

        return result;
    }

    /**
     * 默认净化（trim=true, filter=true）- 对应 PHP purge($string)
     */
    public static String purge(String input) {
        return purge(input, true, true);
    }

    /**
     * 仅去除首尾空格（用于不需要完整净化的场景）
     */
    public static String sanitize(String input) {
        if (input == null) return null;
        return input.trim();
    }

    /**
     * 去除 HTML 标签 - 对应 PHP strip_tags()
     */
    public static String stripTags(String input) {
        if (input == null) return null;
        return input.replaceAll("<[^>]+>", "");
    }

    /**
     * HTML 实体编码 - 对应 PHP htmlentities()
     * 仅编码特殊字符，不做完整 HTML 编码
     */
    public static String htmlEntities(String input) {
        if (input == null) return null;
        return input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#039;");
    }

    /**
     * 转义 LIKE 查询中的通配符（防止 % 和 _ 被滥用为通配符）
     * 用于 MyBatis-Plus 的 .like() 调用前的安全处理
     *
     * @param value 原始搜索关键词
     * @return 转义后的字符串（% → \%, _ → \_, \ → \\）
     */
    public static String escapeLike(String value) {
        if (value == null) return null;
        return value.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }

    /**
     * 净化 API 参数 Map 中的所有值
     */
    public static java.util.Map<String, String> purgeParams(java.util.Map<String, String> params) {
        if (params == null) return null;
        java.util.Map<String, String> result = new java.util.LinkedHashMap<>();
        for (java.util.Map.Entry<String, String> entry : params.entrySet()) {
            result.put(entry.getKey(), purge(entry.getValue(), true, true));
        }
        return result;
    }
}
