package com.easycloud.controller;

import com.easycloud.common.Result;
import com.easycloud.service.CaptchaService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 验证码 Controller
 * <p>
 * GET  /api/captcha         → 获取验证码图片（返回 base64 + captchaId）
 * POST /api/captcha/verify  → 验证验证码
 */
@RestController
@RequestMapping("/api/captcha")
@RequiredArgsConstructor
public class CaptchaController {

    private final CaptchaService captchaService;

    /**
     * 获取验证码
     *
     * @param type 验证码类型: image（默认）, geetest, dingxiang
     * @return captchaId + base64 图片
     */
    @GetMapping
    public Result<Map<String, String>> getCaptcha(@RequestParam(defaultValue = "image") String type) {
        CaptchaService.CaptchaResult result = captchaService.generateCaptcha(type);

        Map<String, String> data = new HashMap<>();
        data.put("captchaId", result.getCaptchaId());
        data.put("image", result.getImageBase64());
        data.put("type", type);

        return Result.ok(data);
    }

    /**
     * 验证验证码
     *
     * @param request 包含 captchaId、code、type
     * @return 验证结果
     */
    @PostMapping("/verify")
    public Result<Void> verifyCaptcha(@RequestBody VerifyRequest request) {
        boolean success = captchaService.verifyCaptcha(
                request.getType(),
                request.getCaptchaId(),
                request.getCode()
        );

        if (success) {
            return Result.ok();
        } else {
            return Result.fail(400, "验证码错误或已过期");
        }
    }

    @Data
    public static class VerifyRequest {
        private String captchaId;
        private String code;
        private String type;
    }
}
