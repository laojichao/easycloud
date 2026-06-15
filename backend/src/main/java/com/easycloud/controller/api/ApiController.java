package com.easycloud.controller.api;

import com.easycloud.common.ApiCrypto;
import com.easycloud.common.ApiSignature;
import com.easycloud.common.ErrorCode;
import com.easycloud.common.InputSanitizer;
import com.easycloud.entity.App;
import com.easycloud.mapper.AppMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.*;

/**
 * API 路由入口控制器
 * <p>
 * 精确复刻原 PHP 项目 api.php + api/app.php 的完整 API 兼容层。
 * 所有客户端 API 请求统一经过此控制器处理，支持以下安全机制：
 * <ul>
 *   <li>数据加密/解密（RC4、AES、Base64 等 6 种类型）</li>
 *   <li>请求签名验证（MD5 签名算法）</li>
 *   <li>时间漂移检查（防重放攻击）</li>
 *   <li>输入参数净化（XSS、SQL 注入防护）</li>
 * </ul>
 * <p>
 * API 兼容层设计思路：
 * <ol>
 *   <li>客户端请求统一到达 /api/legacy 端点</li>
 *   <li>根据 app 参数加载应用配置</li>
 *   <li>白名单接口（ini、notice、getfile）直接处理，无需加密验签</li>
 *   <li>非白名单接口按 miState/miType 配置进行解密、验签、时间校验</li>
 *   <li>根据 api 参数路由到具体 Handler 处理业务逻辑</li>
 *   <li>响应数据按 miType 配置加密后返回</li>
 * </ol>
 * <p>
 * 对应原 PHP 文件: api.php（入口）、api/app.php（路由）
 *
 * @author EasyCloud
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {

    private final AppMapper appMapper;
    private final IniHandler iniHandler;
    private final NoticeHandler noticeHandler;
    private final GetfileHandler getfileHandler;
    private final KmlogonHandler kmlogonHandler;
    private final KmunmachineHandler kmunmachineHandler;
    private final ObjectMapper objectMapper;

    /** 白名单接口 - 不需要加密也不需要签名验证 */
    private static final Set<String> WHITE_LIST = Set.of("ini", "notice", "getfile");

    /**
     * API 入口 - 兼容 GET 和 POST（PHP api.php 使用 $_REQUEST）
     */
    @RequestMapping(value = "/legacy", method = {RequestMethod.GET, RequestMethod.POST})
    public void handleApi(HttpServletRequest request,
                          HttpServletResponse response,
                          @RequestParam(value = "app", required = false) Long appId,
                          @RequestParam(value = "api", required = false) String api,
                          @RequestParam(value = "other", required = false, defaultValue = "index") String action,
                          @RequestParam(value = "sign", required = false) String sign,
                          @RequestParam(value = "data", required = false) String data,
                          @RequestParam(value = "value", required = false) String value) throws IOException {

        response.setContentType("application/json;charset=UTF-8");

        if (api == null || api.isEmpty()) {
            writeResponse(response, buildJsonResponse(101, "缺少api参数", null, null, value));
            return;
        }

        // 加载应用配置
        if (appId == null || appId <= 0) {
            writeResponse(response, buildJsonResponse(101, "应用不存在", null, null, value));
            return;
        }

        App app = appMapper.selectById(appId);
        if (app == null) {
            writeResponse(response, buildJsonResponse(101, "应用不存在", null, null, value));
            return;
        }
        if ("n".equals(app.getActive())) {
            writeResponse(response, buildJsonResponse(102, "应用已关闭", app, null, value));
            return;
        }

        // 更新调用次数（PHP api.php 对所有接口都更新）
        appMapper.updateTotal(appId);

        // 白名单接口直接处理（不解密、不验签，但需要加密响应）
        if (WHITE_LIST.contains(api)) {
            Map<String, String> dataArr = collectParams(request);

            // PHP: if(!$value) $value = $data_arr['value'];
            if (value == null || value.isEmpty()) {
                value = dataArr.get("value");
            }

            Object result = routeApi(api, action, app, dataArr, request, value);

            // PHP: out(200, $data, $app_res) 在 mi_state=y 时会加密响应
            if (result instanceof Map && "y".equals(app.getMiState()) && app.getMiType() > 0) {
                String json = objectMapper.writeValueAsString(result);
                String encrypted = ApiCrypto.encrypt(json, app.getMiType(), app.getRc4Key());
                writeResponse(response, encrypted);
            } else {
                String json = objectMapper.writeValueAsString(result);
                writeResponse(response, json);
            }
            return;
        }

        // 非白名单接口 - 需要解密/验证
        Map<String, String> dataArr;

        if ("y".equals(app.getMiState())) {
            // 数据已加密
            if (app.getMiType() == 0) {
                // 明文模式
                dataArr = collectParams(request);

                // PHP: if(!$value) $value = $data_arr['value'];
                if (value == null || value.isEmpty()) {
                    value = dataArr.get("value");
                }

                // 签名验证
                if ("y".equals(app.getMiSign())) {
                    if (sign == null || sign.isEmpty()) {
                        writeResponse(response, buildJsonResponse(104, "签名为空", app, null, value));
                        return;
                    }
                    if ("y".equals(app.getPrintSign())) {
                        writeResponse(response, ApiSignature.sign(dataArr, app.getAppkey(), false));
                        return;
                    }
                    if (!ApiSignature.verify(dataArr, sign, app.getAppkey())) {
                        writeResponse(response, buildJsonResponse(106, "签名有误", app, null, value));
                        return;
                    }
                }
            } else {
                // 加密模式
                if (data == null || data.isEmpty()) {
                    writeResponse(response, buildJsonResponse(107, "数据为空", app, null, value));
                    return;
                }

                try {
                    String decrypted = ApiCrypto.decrypt(data, app.getMiType(), app.getRc4Key(), app.getMiRsaPrivateKey());
                    dataArr = ApiCrypto.parseKeyValue(decrypted);
                } catch (Exception e) {
                    log.error("数据解密失败", e);
                    writeResponse(response, buildJsonResponse(107, "数据解密失败", app, null, value));
                    return;
                }

                // 获取 value
                if (value == null || value.isEmpty()) {
                    value = dataArr.get("value");
                }

                // 签名验证
                if ("y".equals(app.getMiSign())) {
                    if ("y".equals(app.getMiSignIn())) {
                        // 签名在数据内部
                        String dataSign = dataArr.get("sign");
                        if (dataSign == null || dataSign.isEmpty()) {
                            writeResponse(response, buildJsonResponse(104, "签名为空", app, null, value));
                            return;
                        }
                        if ("y".equals(app.getPrintSign())) {
                            writeResponse(response, ApiSignature.sign(dataArr, app.getAppkey(), false));
                            return;
                        }
                        if (!ApiSignature.verify(dataArr, dataSign, app.getAppkey())) {
                            writeResponse(response, buildJsonResponse(106, "签名有误", app, null, value));
                            return;
                        }
                    } else {
                        // 签名在外部参数
                        if (sign == null || sign.isEmpty()) {
                            writeResponse(response, buildJsonResponse(104, "签名为空", app, null, value));
                            return;
                        }
                        if ("y".equals(app.getPrintSign())) {
                            writeResponse(response, ApiSignature.sign(dataArr, app.getAppkey(), false));
                            return;
                        }
                        if (!ApiSignature.verify(dataArr, sign, app.getAppkey())) {
                            writeResponse(response, buildJsonResponse(106, "签名有误", app, null, value));
                            return;
                        }
                    }
                }

                // 时间漂移检查
                if (app.getMiTime() != null && app.getMiTime() > 0) {
                    String clientTime = dataArr.get("t");
                    if (clientTime == null || clientTime.isEmpty()) {
                        writeResponse(response, buildJsonResponse(108, "未发现时间变量", app, null, value));
                        return;
                    }
                    try {
                        long clientTs = Long.parseLong(clientTime.trim());
                        long timeDiff = Instant.now().getEpochSecond() - clientTs;
                        if (timeDiff > app.getMiTime()) {
                            writeResponse(response, buildJsonResponse(105, "数据过期", app, null, value));
                            return;
                        }
                    } catch (NumberFormatException e) {
                        writeResponse(response, buildJsonResponse(108, "未发现时间变量", app, null, value));
                        return;
                    }
                }
            }
        } else {
            // 未加密
            dataArr = collectParams(request);
        }

        // 路由到具体 handler
        Object result = routeApi(api, action, app, dataArr, request, value);

        // 加密输出（非白名单接口才加密）
        if (result instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> resultMap = (Map<String, Object>) result;
            String json = objectMapper.writeValueAsString(resultMap);

            if ("y".equals(app.getMiState()) && app.getMiType() > 0) {
                // PHP: 整个JSON加密后直接输出原始字符串（不包裹JSON）
                String encrypted = ApiCrypto.encrypt(json, app.getMiType(), app.getRc4Key());
                writeResponse(response, encrypted);
                return;
            }

            writeResponse(response, json);
        } else {
            writeResponse(response, objectMapper.writeValueAsString(result));
        }
    }

    /**
     * 收集请求参数（兼容 GET 和 POST）- 对应 PHP purge() 净化
     */
    private Map<String, String> collectParams(HttpServletRequest request) {
        Map<String, String> params = new LinkedHashMap<>();
        Map<String, String[]> paramMap = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
            if (entry.getValue() != null && entry.getValue().length > 0) {
                // PHP: purge() 去除内部空格、XSS、SQL关键字
                params.put(entry.getKey(), InputSanitizer.purge(entry.getValue()[0], true, true));
            }
        }
        return params;
    }

    /**
     * 路由到具体的 API Handler
     */
    private Object routeApi(String api, String action, App app, Map<String, String> dataArr, HttpServletRequest request, String value) {
        switch (api) {
            case "ini":
                return iniHandler.handle(app, dataArr, value);
            case "notice":
                return noticeHandler.handle(app, dataArr, value);
            case "getfile":
                return getfileHandler.handle(app, dataArr, value);
            case "kmlogon":
                return kmlogonHandler.handle(app, dataArr, request, value);
            case "kmunmachine":
                return kmunmachineHandler.handle(app, dataArr, request, value);
            default:
                return buildJsonResponseMap(100, "请绑定应用ID", app, null, value);
        }
    }

    /**
     * 根据错误码自动查找消息 - 对应 PHP out($code) 自动从 msg.php 查找
     */
    private String buildJsonResponse(int code, App app, String value) {
        return buildJsonResponse(code, ErrorCode.getMessage(code), app, null, value);
    }

    /**
     * 构建 JSON 响应字符串 - 兼容 PHP out() 函数
     */
    private String buildJsonResponse(int code, Object msg, App app, Object data, String value) {
        try {
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("code", code);
            // PHP: msg 可以是字符串也可以是数据对象
            result.put("msg", msg);
            long now = Instant.now().getEpochSecond();
            result.put("time", now);
            if (app != null) {
                result.put("check", ApiSignature.responseCheck(now, app.getAppkey(), value != null ? value : ""));
            }
            return objectMapper.writeValueAsString(result);
        } catch (Exception e) {
            return "{\"code\":500,\"msg\":\"服务器内部错误\",\"time\":" + Instant.now().getEpochSecond() + "}";
        }
    }

    /**
     * 构建响应 Map（供 handler 使用）
     */
    public static Map<String, Object> buildJsonResponseMap(int code, Object msg, App app, Object data, String value) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", code);
        result.put("msg", msg);
        long now = Instant.now().getEpochSecond();
        result.put("time", now);
        if (app != null) {
            result.put("check", ApiSignature.responseCheck(now, app.getAppkey(), value != null ? value : ""));
        }
        return result;
    }

    /**
     * 构建成功响应（数据放在 msg 字段，兼容 PHP out(200, $data, $app)）
     */
    public static Map<String, Object> buildSuccessResponse(Object data, App app, String value) {
        return buildJsonResponseMap(200, data, app, null, value);
    }

    /**
     * 构建错误响应
     */
    public static Map<String, Object> buildErrorResponse(int code, String msg, App app, String value) {
        return buildJsonResponseMap(code, msg, app, null, value);
    }

    private void writeResponse(HttpServletResponse response, String content) throws IOException {
        response.getWriter().write(content);
        response.getWriter().flush();
    }
}
