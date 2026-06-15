package com.easycloud.common;

import java.util.HashMap;
import java.util.Map;

/**
 * XML 工具类 - 微信/QQ支付 XML 格式互转
 * <p>
 * 提供 Map 与 XML 之间的相互转换，兼容微信支付和QQ支付的消息格式。
 * 此工具类替代了之前分散在 PayController 和 PaymentService 中的重复实现。
 *
 * @author EasyCloud
 * @since 1.0.0
 */
public final class XmlUtil {

    private XmlUtil() {
        // 工具类，禁止实例化
    }

    /**
     * Map 转 XML（微信/QQ支付格式）
     * <p>
     * 每个键值对转换为 {@code <key><![CDATA[value]]></key>} 格式，
     * 外层包裹 {@code <xml>} 标签。
     *
     * @param params 参数 Map
     * @return XML 字符串
     */
    public static String mapToXml(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append("<").append(entry.getKey()).append(">");
            sb.append("<![CDATA[").append(entry.getValue()).append("]]>");
            sb.append("</").append(entry.getKey()).append(">");
        }
        sb.append("</xml>");
        return sb.toString();
    }

    /**
     * XML 转 Map（解析微信/QQ支付回调）
     * <p>
     * 简单 XML 解析，兼容微信/QQ支付返回格式。
     * 支持 CDATA 和普通文本两种值格式。
     *
     * @param xml XML 字符串
     * @return 解析后的 Map
     */
    public static Map<String, String> xmlToMap(String xml) {
        Map<String, String> map = new HashMap<>();
        if (xml == null || xml.isEmpty()) {
            return map;
        }
        String[] tags = xml.split("<");
        for (String tag : tags) {
            if (tag.isEmpty()) continue;
            int closeIdx = tag.indexOf(">");
            if (closeIdx <= 0) continue;
            String key = tag.substring(0, closeIdx);
            String value;
            int cdataStart = tag.indexOf("![CDATA[");
            if (cdataStart >= 0) {
                int cdataEnd = tag.indexOf("]]", cdataStart);
                value = tag.substring(cdataStart + 8, cdataEnd);
            } else {
                int endTag = tag.indexOf("</" + key);
                if (endTag > 0) {
                    value = tag.substring(closeIdx + 1, endTag);
                } else {
                    continue;
                }
            }
            if (!key.equals("xml")) {
                map.put(key, value);
            }
        }
        return map;
    }
}
