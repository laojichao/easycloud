package com.easycloud.common;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 轻量 XML 工具 - 用于支付回调报文与应答报文的相互转换
 */
public final class XmlUtil {

    private static final Pattern NODE_PATTERN = Pattern.compile("<([a-zA-Z0-9_]+)><!\\[CDATA\\[(.*?)\\]\\]></\\1>|<([a-zA-Z0-9_]+)>([^<]*)</\\3>");

    private XmlUtil() {
    }

    public static String mapToXml(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>");
        for (Map.Entry<String, String> e : map.entrySet()) {
            String value = e.getValue() == null ? "" : e.getValue();
            sb.append("<").append(e.getKey()).append(">");
            sb.append("<![CDATA[").append(value).append("]]>");
            sb.append("</").append(e.getKey()).append(">");
        }
        sb.append("</xml>");
        return sb.toString();
    }

    public static Map<String, String> xmlToMap(String xml) {
        Map<String, String> map = new LinkedHashMap<>();
        if (xml == null || xml.isEmpty()) {
            return map;
        }
        Matcher m = NODE_PATTERN.matcher(xml);
        while (m.find()) {
            String key = m.group(1) != null ? m.group(1) : m.group(3);
            String value = m.group(2) != null ? m.group(2) : m.group(4);
            if (key != null) {
                map.put(key, value == null ? "" : value);
            }
        }
        return map;
    }
}
