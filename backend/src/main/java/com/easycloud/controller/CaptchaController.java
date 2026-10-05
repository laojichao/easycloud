package com.easycloud.controller;

import com.easycloud.common.Result;
import com.easycloud.service.CaptchaService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 图形验证码 - 对应 PHP admin/code.php
 */
@RestController
@RequestMapping("/api/captcha")
@RequiredArgsConstructor
public class CaptchaController {

    private final CaptchaService captchaService;

    @GetMapping
    public Result<?> getCaptcha(@RequestParam(defaultValue = "image") String type) {
        Map<String, Object> data = new HashMap<>();
        boolean enabled = captchaService.isLoginCaptchaEnabled();
        data.put("enabled", enabled);
        if (enabled) {
            CaptchaService.CaptchaResult captcha = captchaService.generateCaptcha(type);
            data.put("captchaId", captcha.getCaptchaId());
            data.put("image", captcha.getImage());
        }
        return Result.ok(data);
    }

    @PostMapping("/verify")
    public Result<?> verify(@RequestBody VerifyRequest body) {
        boolean success = body != null && captchaService.verifyCaptcha(body.getCaptchaId(), body.getCode());
        Map<String, Object> data = new HashMap<>();
        data.put("success", success);
        return Result.ok(data);
    }

    @Data
    public static class VerifyRequest {
        private String captchaId;
        private String code;
    }
}
