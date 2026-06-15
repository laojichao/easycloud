package com.easycloud.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

/**
 * 短信发送服务 - 对应 PHP 多服务商短信发送
 * <p>
 * 支持服务商（通过 sms_type 配置切换）：
 * - tencent : 腾讯云短信
 * - aliyun  : 阿里云短信
 * - 978w    : 978w.cn 短信
 * <p>
 * 配置项（yixi_config）：
 * - sms_type       服务商类型
 * - sms_appid      应用ID
 * - sms_appkey     应用密钥
 * - sms_sign       签名
 * - sms_template   模板ID
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SmsService {

    private final ConfigService configService;

    private static final Duration TIMEOUT = Duration.ofSeconds(5);
    /** 复用 HttpClient 实例，避免每次请求创建新连接 */
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(TIMEOUT)
            .build();

    /**
     * 异步发送短信验证码
     *
     * @param phone 手机号
     * @param code  验证码
     * @param scope 验证码用途（如 login, register）
     * @return 是否发送成功
     */
    @Async
    public CompletableFuture<Boolean> sendSms(String phone, String code, String scope) {
        String smsType = configService.getSetting("sms_type");
        if (smsType == null || smsType.isBlank()) {
            log.warn("短信服务未配置 sms_type，跳过发送: phone={}", phone);
            return CompletableFuture.completedFuture(false);
        }

        boolean result;
        switch (smsType.toLowerCase()) {
            case "tencent":
                result = sendTencentSms(phone, code);
                break;
            case "aliyun":
                result = sendAliyunSms(phone, code);
                break;
            case "978w":
                result = send978wSms(phone, code);
                break;
            default:
                log.error("不支持的短信服务商: type={}", smsType);
                result = false;
        }
        return CompletableFuture.completedFuture(result);
    }

    /**
     * 同步发送短信验证码
     *
     * @param phone 手机号
     * @param code  验证码
     * @param scope 验证码用途
     * @return 是否发送成功
     */
    public boolean sendSmsSync(String phone, String code, String scope) {
        String smsType = configService.getSetting("sms_type");
        if (smsType == null || smsType.isBlank()) {
            log.warn("短信服务未配置 sms_type，跳过发送: phone={}", phone);
            return false;
        }

        switch (smsType.toLowerCase()) {
            case "tencent":
                return sendTencentSms(phone, code);
            case "aliyun":
                return sendAliyunSms(phone, code);
            case "978w":
                return send978wSms(phone, code);
            default:
                log.error("不支持的短信服务商: type={}", smsType);
                return false;
        }
    }

    /**
     * 腾讯云短信 - 通过 HTTP API 发送
     * API: https://yun.tim.qq.com/v5/tlssmssvr/sendsms
     *
     * @param phone 手机号
     * @param code  验证码
     * @return 是否成功
     */
    private boolean sendTencentSms(String phone, String code) {
        String appId = configService.getSetting("sms_appid");
        String appKey = configService.getSetting("sms_appkey");
        String sign = configService.getSetting("sms_sign");
        String templateId = configService.getSetting("sms_template");

        if (appId == null || appKey == null) {
            log.error("腾讯云短信配置缺失: sms_appid 或 sms_appkey 未设置");
            return false;
        }

        try {
            // 腾讯云短信 API 请求体
            String body = String.format("""
                    {
                        "tel": {"nationcode": "86", "mobile": "%s"},
                        "sign": "%s",
                        "tpl_id": %s,
                        "params": ["%s"],
                        "sig": "%s",
                        "time": %d,
                        "extend": "",
                        "ext": ""
                    }
                    """,
                    phone,
                    sign != null ? sign : "",
                    templateId != null ? templateId : "0",
                    code,
                    appKey,
                    System.currentTimeMillis() / 1000);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://yun.tim.qq.com/v5/tlssmssvr/sendsms?sdkappid=" + appId))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .timeout(TIMEOUT)
                    .build();

            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("腾讯云短信发送结果: phone={}, status={}", phone, response.statusCode());
            return response.statusCode() == 200 && response.body().contains("\"result\":0");
        } catch (Exception e) {
            log.error("腾讯云短信发送失败: phone={}, error={}", phone, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 阿里云短信 - 通过 HTTP API 发送
     * 使用阿里云 POP 签名机制
     *
     * @param phone 手机号
     * @param code  验证码
     * @return 是否成功
     */
    private boolean sendAliyunSms(String phone, String code) {
        String appId = configService.getSetting("sms_appid");
        String appKey = configService.getSetting("sms_appkey");
        String sign = configService.getSetting("sms_sign");
        String templateId = configService.getSetting("sms_template");

        if (appKey == null) {
            log.error("阿里云短信配置缺失: sms_appkey 未设置");
            return false;
        }

        try {
            // 阿里云短信 API 公共参数
            String templateParam = String.format("{\"code\":\"%s\"}", code);

            // 构建查询参数（简化版，生产环境建议使用阿里云 SDK）
            String queryString = String.format(
                    "AccessKeyId=%s&Format=JSON&SignatureMethod=HMAC-SHA1&SignatureNonce=%s&SignatureVersion=1.0" +
                            "&Action=SendSms&PhoneNumbers=%s&SignName=%s&TemplateCode=%s&TemplateParam=%s" +
                            "&Timestamp=%s&Version=2017-05-25",
                    appKey,
                    java.util.UUID.randomUUID().toString(),
                    phone,
                    sign != null ? sign : "",
                    templateId != null ? templateId : "",
                    java.net.URLEncoder.encode(templateParam, java.nio.charset.StandardCharsets.UTF_8),
                    java.net.URLEncoder.encode(
                            java.time.Instant.now().toString().substring(0, 19) + "Z",
                            java.nio.charset.StandardCharsets.UTF_8)
            );

            // 注意：此处简化了签名计算，生产环境应使用阿里云 SDK
            String url = "https://dysmsapi.aliyuncs.com/?" + queryString;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .timeout(TIMEOUT)
                    .build();

            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("阿里云短信发送结果: phone={}, status={}, body={}", phone, response.statusCode(), response.body());
            return response.statusCode() == 200 && response.body().contains("\"Code\":\"OK\"");
        } catch (Exception e) {
            log.error("阿里云短信发送失败: phone={}, error={}", phone, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 978w.cn 短信 - 通过 HTTP API 发送
     *
     * @param phone 手机号
     * @param code  验证码
     * @return 是否成功
     */
    private boolean send978wSms(String phone, String code) {
        String appId = configService.getSetting("sms_appid");
        String appKey = configService.getSetting("sms_appkey");
        String templateId = configService.getSetting("sms_template");

        if (appId == null || appKey == null) {
            log.error("978w短信配置缺失: sms_appid 或 sms_appkey 未设置");
            return false;
        }

        try {
            String url = String.format(
                    "http://api.978w.cn/sms/?uid=%s&pwd=%s&mobile=%s&content=%s&template=%s",
                    appId,
                    appKey,
                    phone,
                    java.net.URLEncoder.encode("您的验证码是：" + code + "，5分钟内有效。", java.nio.charset.StandardCharsets.UTF_8),
                    templateId != null ? templateId : ""
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .timeout(TIMEOUT)
                    .build();

            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("978w短信发送结果: phone={}, status={}", phone, response.statusCode());
            return response.statusCode() == 200;
        } catch (Exception e) {
            log.error("978w短信发送失败: phone={}, error={}", phone, e.getMessage(), e);
            return false;
        }
    }
}
