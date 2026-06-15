package com.easycloud.common;

import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 蓝奏云链接解析器
 * <p>
 * 精确复刻原 PHP 项目的蓝奏云文件分享链接解析逻辑。
 * 将蓝奏云分享链接（https://www.lanzoui.com/xxxxx）解析为直接下载链接。
 * <p>
 * 解析流程：
 * <ol>
 *   <li>从分享链接中提取文件 ID</li>
 *   <li>访问分享页面获取页面内容</li>
 *   <li>判断是否需要密码</li>
 *   <li>从页面 JavaScript 中提取下载链接变量</li>
 *   <li>跟踪重定向获取最终下载地址</li>
 * </ol>
 * <p>
 * 对应原 PHP 文件: includes/global.php 中的 lanzou(), send_post(), getRedirect() 函数
 *
 * @author EasyCloud
 * @since 1.0.0
 */
@Slf4j
public class LanzouResolver {

    /** HTTP 客户端实例（连接超时 15 秒，自动跟踪重定向） */
    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    /** 模拟 iPhone 浏览器的 User-Agent（与原 PHP 保持一致） */
    private static final String USER_AGENT = "Mozilla/5.0 (iPhone; CPU iPhone OS 6_0 like Mac X) AppleWebKit/536.26 (KHTML, like Gecko) Version/6.0 Mobile/10A5376e Safari/8536.25";

    /**
     * 解析蓝奏云链接 - 对应 PHP lanzou($url, $pwd)
     *
     * @param url 蓝奏云分享链接
     * @param pwd 分享密码（可选）
     * @return 直接下载链接，失败返回原始URL
     */
    public static String resolve(String url, String pwd) {
        if (url == null || url.isEmpty()) {
            return url;
        }

        try {
            // 1. 提取文件 ID（PHP: GetBetween($url, 'com/', '/')）
            String id = extractId(url);
            if (id.isEmpty()) {
                log.warn("无法从URL提取蓝奏云ID: {}", url);
                return url;
            }

            // 2. 获取分享页面
            String pageUrl = "https://www.lanzoui.com/tp/" + id;
            String pageContent = httpGet(pageUrl);

            if (pageContent == null || pageContent.isEmpty() || pageContent.contains("文件取消分享了")) {
                log.warn("蓝奏云文件已取消分享: {}", url);
                return "文件取消分享了";
            }

            // 3. 需要密码但未提供
            if (pageContent.contains("输入密码") && (pwd == null || pwd.isEmpty())) {
                log.warn("蓝奏云文件需要密码: {}", url);
                return "未填写分享密码";
            }

            // 4. 提取下载链接（无密码情况）
            String download = null;

            // 匹配 var domianload = '...';
            Pattern p1 = Pattern.compile("var domianload = '(.*?)';");
            Matcher m1 = p1.matcher(pageContent);

            // 匹配 domianload + '...';
            Pattern p2 = Pattern.compile("domianload \\+ '(.*?)'");
            Matcher m2 = p2.matcher(pageContent);

            // 匹配 var downloads = '...';
            Pattern p3 = Pattern.compile("var downloads = '(.*?)'");
            Matcher m3 = p3.matcher(pageContent);

            if (m1.find()) {
                String part1 = m1.group(1);
                if (m2.find()) {
                    download = getRedirect(part1 + m2.group(1));
                } else if (m3.find()) {
                    download = getRedirect(part1 + m3.group(1));
                }
            }

            // 5. 有密码的情况
            if (pwd != null && !pwd.isEmpty()) {
                Pattern signPattern = Pattern.compile("sign':'(.*?)'");
                Matcher signMatcher = signPattern.matcher(pageContent);
                if (signMatcher.find()) {
                    String sign = signMatcher.group(1);
                    String postData = "action=downprocess&sign=" + URLEncoder.encode(sign, StandardCharsets.UTF_8)
                            + "&p=" + URLEncoder.encode(pwd, StandardCharsets.UTF_8);
                    String pwdResponse = httpPost("https://wwa.lanzoui.com/ajaxm.php", postData);

                    if (pwdResponse != null && pwdResponse.contains("\"zt\":0")) {
                        return "分享密码不正确";
                    }

                    if (pwdResponse != null) {
                        // 解析 JSON 响应
                        String dom = extractJsonField(pwdResponse, "dom");
                        String fileUrl = extractJsonField(pwdResponse, "url");
                        if (dom != null && fileUrl != null) {
                            download = getRedirect(dom + "/file/" + fileUrl);
                        }
                    }
                }
            }

            return download != null ? download : url;

        } catch (Exception e) {
            log.error("蓝奏云解析失败: {}", url, e);
            return url;
        }
    }

    /**
     * 从 URL 提取蓝奏云文件 ID
     * PHP: GetBetween($url, 'com/', '/')
     */
    private static String extractId(String url) {
        int comIdx = url.indexOf("com/");
        if (comIdx < 0) return "";

        int start = comIdx + 4;
        int end = url.indexOf("/", start);
        if (end < 0) end = url.length();

        return url.substring(start, end);
    }

    /**
     * HTTP GET 请求
     */
    private static String httpGet(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", USER_AGENT)
                    .header("Accept", "*/*")
                    .header("Accept-Encoding", "gzip,deflate,sdch")
                    .header("Accept-Language", "zh-CN,zh;q=0.8")
                    .header("Connection", "close")
                    .GET()
                    .build();

            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            log.warn("HTTP GET 失败: {}", url, e);
            return null;
        }
    }

    /**
     * HTTP POST 请求 - 对应 PHP send_post()
     */
    private static String httpPost(String url, String formData) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", USER_AGENT)
                    .header("Referer", "https://www.lanzoui.com/")
                    .header("Accept-Language", "zh-CN,zh;q=0.9")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(formData))
                    .build();

            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            log.warn("HTTP POST 失败: {}", url, e);
            return null;
        }
    }

    /**
     * 跟踪重定向获取最终 URL - 对应 PHP getRedirect()
     */
    private static String getRedirect(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", USER_AGENT)
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .header("Accept-Encoding", "gzip, deflate")
                    .header("Accept-Language", "zh-CN,zh;q=0.9")
                    .header("Cache-Control", "no-cache")
                    .header("Connection", "keep-alive")
                    .GET()
                    .build();

            HttpResponse<Void> response = CLIENT.send(request, HttpResponse.BodyHandlers.discarding());
            // 获取重定向后的 URI
            return response.uri().toString();
        } catch (Exception e) {
            log.warn("重定向跟踪失败: {}", url, e);
            return url;
        }
    }

    /**
     * 简单提取 JSON 字段值
     */
    private static String extractJsonField(String json, String field) {
        Pattern pattern = Pattern.compile("\"" + field + "\":\"(.*?)\"");
        Matcher matcher = pattern.matcher(json);
        return matcher.find() ? matcher.group(1) : null;
    }
}
