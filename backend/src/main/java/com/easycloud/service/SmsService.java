package com.easycloud.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.Base64;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;

/**
 * 短信服务 - 对应 PHP send_sms()：腾讯云(旧版API) / 阿里云 / 978w 三通道
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SmsService {

    private final ConfigService configService;
    private static final Duration TIMEOUT = Duration.ofSeconds(10);
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().connectTimeout(TIMEOUT).build();

    public CompletableFuture<Boolean> sendSms(String phone, String code, String scope) {
        return CompletableFuture.supplyAsync(() -> sendSmsSync(phone, code, scope));
    }

    public boolean sendSmsSync(String phone, String code, String scope) {
        try {
            int api = 0;
            try {
                api = Integer.parseInt(orDefault(configService.getSetting("sms_api"), "0"));
            } catch (NumberFormatException ignored) {
            }
            boolean ok;
            if (api == 1) {
                ok = sendTencentSms(phone, code);
            } else if (api == 2) {
                ok = sendAliyunSms(phone, code);
            } else {
                ok = send978wSms(phone, code, scope);
            }
            if (!ok) {
                log.warn("短信发送失败 phone={} scope={}", phone, scope);
            }
            return ok;
        } catch (Exception e) {
            log.error("短信发送异常 phone={}: {}", phone, e.getMessage());
            return false;
        }
    }

    /** 腾讯云旧版 SMS API（sdkappid + appkey，sig = sha256(appkey+phone+time)） */
    private boolean sendTencentSms(String phone, String code) {
        String appid = configService.getSetting("sms_appid");
        String appkey = configService.getSetting("sms_appkey");
        String sign = configService.getSetting("sms_sign");
        String tpl = orDefault(configService.getSetting("sms_tpl_reg"), configService.getSetting("sms_tpl_login"));
        if (isBlank(appid) || isBlank(appkey) || isBlank(tpl)) {
            return false;
        }
        long time = System.currentTimeMillis() / 1000;
        int random = (int) (Math.random() * 900000 + 100000);
        String sig = sha256Hex(appkey + phone + time);
        String body = "{\"tel\":{\"nationcode\":\"86\",\"phone\":\"" + phone
                + "\"},\"sign\":\"" + nz(sign) + "\",\"tpl_id\":" + tpl
                + ",\"params\":[\"" + code + "\"],\"time\":" + time + ",\"sig\":\"" + sig + "\"}";
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://yun.tim.qq.com/v5/tlssmssvr/sendsms?sdkappid=" + appid + "&random=" + random))
                    .timeout(TIMEOUT)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            HttpResponse<String> resp = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            return resp.body().contains("\"result\":0");
        } catch (Exception e) {
            log.error("腾讯云短信发送失败: {}", e.getMessage());
            return false;
        }
    }

    /** 阿里云短信 RPC 签名（HMAC-SHA1） */
    private boolean sendAliyunSms(String phone, String code) {
        String appid = configService.getSetting("sms_appid");
        String appkey = configService.getSetting("sms_appkey");
        String sign = configService.getSetting("sms_sign");
        String tpl = orDefault(configService.getSetting("sms_tpl_reg"), configService.getSetting("sms_tpl_login"));
        if (isBlank(appid) || isBlank(appkey) || isBlank(tpl)) {
            return false;
        }
        try {
            TreeMap<String, String> params = new TreeMap<>();
            params.put("AccessKeyId", appid);
            params.put("Action", "SendSms");
            params.put("Format", "JSON");
            params.put("PhoneNumbers", phone);
            params.put("RegionId", "cn-hangzhou");
            params.put("SignName", nz(sign));
            params.put("SignatureMethod", "HMAC-SHA1");
            params.put("SignatureNonce", java.util.UUID.randomUUID().toString());
            params.put("SignatureVersion", "1.0");
            params.put("Timestamp", getAliyunTimestamp());
            params.put("TemplateCode", tpl);
            params.put("TemplateParam", "{\"code\":\"" + code + "\"}");
            params.put("Version", "2017-05-25");

            StringBuilder canonical = new StringBuilder();
            params.forEach((k, v) -> canonical.append(percentEncode(k)).append("=").append(percentEncode(v)));
            String stringToSign = "GET&" + percentEncode("/") + "&" + percentEncode(canonical.toString());
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec((appkey + "&").getBytes(StandardCharsets.UTF_8), "HmacSHA1"));
            String signature = Base64.getEncoder().encodeToString(mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8)));

            StringBuilder url = new StringBuilder("https://dysmsapi.aliyuncs.com/?Signature=" + percentEncode(signature));
            params.forEach((k, v) -> url.append("&").append(percentEncode(k)).append("=").append(percentEncode(v)));
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url.toString()))
                    .timeout(TIMEOUT)
                    .GET().build();
            HttpResponse<String> resp = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            return resp.body().contains("\"Code\":\"OK\"");
        } catch (Exception e) {
            log.error("阿里云短信发送失败: {}", e.getMessage());
            return false;
        }
    }

    /** 978w 通道 - 对应 PHP 默认分支 */
    private boolean send978wSms(String phone, String code, String scope) {
        String appkey = configService.getSetting("sms_appkey");
        String tpl = orDefault(configService.getSetting("sms_tpl_" + scope), orDefault(
                configService.getSetting("sms_tpl_reg"), configService.getSetting("sms_tpl_login")));
        String app = orDefault(configService.getSetting("sitename"), "EasyCloud");
        if (isBlank(appkey)) {
            return false;
        }
        try {
            String url = "http://api.978w.cn/yzmsms/index/appkey/" + appkey
                    + "/phone/" + phone + "/moban/" + nz(tpl) + "/app/"
                    + URLEncoder.encode(app, StandardCharsets.UTF_8) + "/code/" + code;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(TIMEOUT)
                    .GET().build();
            HttpResponse<String> resp = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            return resp.body().contains("\"status\":\"200\"") || resp.body().contains("200");
        } catch (Exception e) {
            log.error("978w 短信发送失败: {}", e.getMessage());
            return false;
        }
    }

    private String sha256Hex(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(64);
            for (byte b : hash) {
                sb.append(String.format("%02x", b & 0xFF));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    private String percentEncode(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8)
                .replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
    }

    private String getAliyunTimestamp() {
        return java.time.ZonedDateTime.now(java.time.ZoneOffset.UTC)
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
    }

    private String orDefault(String value, String fallback) {
        return (value == null || value.isBlank()) ? fallback : value;
    }

    private String nz(String value) {
        return value == null ? "" : value;
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
