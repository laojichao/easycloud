package com.easycloud.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import java.util.UUID;

/**
 * 图形验证码 - 对应 PHP ValidateCode.class.php / admin/code.php
 * Redis 存储验证码，5 分钟有效，验证后立即失效
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CaptchaService {

    private final StringRedisTemplate stringRedisTemplate;
    private final ConfigService configService;

    private static final String CAPTCHA_PREFIX = "easycloud:captcha:";
    private static final long CAPTCHA_EXPIRE_MINUTES = 5;
    private static final int CAPTCHA_LENGTH = 4;
    private static final int IMAGE_WIDTH = 120;
    private static final int IMAGE_HEIGHT = 40;
    private static final String CAPTCHA_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private final SecureRandom random = new SecureRandom();

    public boolean isLoginCaptchaEnabled() {
        return "1".equals(configService.getSetting("login_captcha_open"));
    }

    @Data
    @AllArgsConstructor
    public static class CaptchaResult {
        private String captchaId;
        private String image;
    }

    public CaptchaResult generateCaptcha(String type) {
        String code = generateRandomCode();
        String captchaId = UUID.randomUUID().toString().replace("-", "");
        stringRedisTemplate.opsForValue().set(
                CAPTCHA_PREFIX + captchaId,
                code,
                Duration.ofMinutes(CAPTCHA_EXPIRE_MINUTES));
        String image = generateCaptchaImage(code);
        return new CaptchaResult(captchaId, image);
    }

    /**
     * 校验验证码（一次性，验证后立即删除）
     */
    public boolean verifyCaptcha(String captchaId, String code) {
        if (!StringUtils.hasText(captchaId) || !StringUtils.hasText(code)) {
            return false;
        }
        String key = CAPTCHA_PREFIX + captchaId;
        String stored = stringRedisTemplate.opsForValue().get(key);
        stringRedisTemplate.delete(key);
        return stored != null && stored.equalsIgnoreCase(code.trim());
    }

    private CaptchaResult generateImageCaptcha() {
        String code = generateRandomCode();
        String image = generateCaptchaImage(code);
        return new CaptchaResult(code, image);
    }

    private String generateRandomCode() {
        StringBuilder sb = new StringBuilder(CAPTCHA_LENGTH);
        for (int i = 0; i < CAPTCHA_LENGTH; i++) {
            sb.append(CAPTCHA_CHARS.charAt(random.nextInt(CAPTCHA_CHARS.length())));
        }
        return sb.toString();
    }

    private String generateCaptchaImage(String code) {
        BufferedImage image = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(new Color(245, 247, 250));
            g.fillRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
            // 干扰线
            for (int i = 0; i < 5; i++) {
                g.setColor(new Color(180 + random.nextInt(60), 180 + random.nextInt(60), 180 + random.nextInt(60)));
                g.setStroke(new BasicStroke(1 + random.nextInt(2)));
                g.drawLine(random.nextInt(IMAGE_WIDTH), random.nextInt(IMAGE_HEIGHT),
                        random.nextInt(IMAGE_WIDTH), random.nextInt(IMAGE_HEIGHT));
            }
            // 字符
            g.setFont(new Font("Arial", Font.BOLD, 26));
            for (int i = 0; i < code.length(); i++) {
                g.setColor(new Color(30 + random.nextInt(80), 30 + random.nextInt(80), 30 + random.nextInt(80)));
                double theta = (random.nextDouble() - 0.5) * 0.5;
                int x = 12 + i * ((IMAGE_WIDTH - 24) / CAPTCHA_LENGTH);
                int y = IMAGE_HEIGHT / 2 + 9;
                g.rotate(theta, x, y);
                g.drawString(String.valueOf(code.charAt(i)), x, y);
                g.rotate(-theta, x, y);
            }
            // 噪点
            for (int i = 0; i < 60; i++) {
                image.setRGB(random.nextInt(IMAGE_WIDTH), random.nextInt(IMAGE_HEIGHT),
                        new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)).getRGB());
            }
        } finally {
            g.dispose();
        }
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(image, "png", out);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(out.toByteArray());
        } catch (Exception e) {
            log.error("生成验证码图片失败", e);
            return "";
        }
    }
}
