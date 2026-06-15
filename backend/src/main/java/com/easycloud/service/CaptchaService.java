package com.easycloud.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 验证码服务 - 对应 PHP 验证码系统
 * <p>
 * 支持三种验证码类型：
 * 1. image    - 自定义图片验证码（内置 Java Graphics2D 实现）
 * 2. geetest  - Geetest 极验验证码（预留 TODO）
 * 3. dingxiang - DingXiang 顶象验证码（预留 TODO）
 * <p>
 * 验证码存储在 Redis 中，5 分钟过期。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CaptchaService {

    private final StringRedisTemplate stringRedisTemplate;

    /** Redis 缓存键前缀 */
    private static final String CAPTCHA_PREFIX = "easycloud:captcha:";
    /** 验证码有效期（分钟） */
    private static final long CAPTCHA_EXPIRE_MINUTES = 5;
    /** 验证码字符长度 */
    private static final int CAPTCHA_LENGTH = 4;
    /** 验证码图片宽度（像素） */
    private static final int IMAGE_WIDTH = 120;
    /** 验证码图片高度（像素） */
    private static final int IMAGE_HEIGHT = 40;

    /** 验证码可用字符集（排除了容易混淆的 0/O/1/I/l） */
    private static final String CAPTCHA_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private final SecureRandom random = new SecureRandom();

    /**
     * 生成验证码
     *
     * @param type 验证码类型: image, geetest, dingxiang
     * @return CaptchaResult 包含 captchaId 和验证码数据（图片为 base64）
     */
    public CaptchaResult generateCaptcha(String type) {
        if (type == null) {
            type = "image";
        }

        switch (type.toLowerCase()) {
            case "image":
                return generateImageCaptcha();
            case "geetest":
                // TODO: Geetest 极验验证码集成
                log.warn("Geetest 验证码暂未实现，回退到图片验证码");
                return generateImageCaptcha();
            case "dingxiang":
                // TODO: DingXiang 顶象验证码集成
                log.warn("DingXiang 验证码暂未实现，回退到图片验证码");
                return generateImageCaptcha();
            default:
                return generateImageCaptcha();
        }
    }

    /**
     * 验证验证码
     *
     * @param type      验证码类型
     * @param captchaId 验证码 ID
     * @param code      用户输入的验证码
     * @return 是否验证成功
     */
    public boolean verifyCaptcha(String type, String captchaId, String code) {
        if (captchaId == null || code == null) {
            return false;
        }

        if ("geetest".equals(type)) {
            // TODO: Geetest 极验验证码校验
            log.warn("Geetest 验证码校验暂未实现");
            return false;
        }
        if ("dingxiang".equals(type)) {
            // TODO: DingXiang 顶象验证码校验
            log.warn("DingXiang 验证码校验暂未实现");
            return false;
        }

        // 图片验证码校验
        String cacheKey = CAPTCHA_PREFIX + captchaId;
        String cachedCode = stringRedisTemplate.opsForValue().get(cacheKey);
        if (cachedCode == null) {
            log.warn("验证码已过期或不存在: captchaId={}", captchaId);
            return false;
        }

        // 删除已使用的验证码（一次性）
        stringRedisTemplate.delete(cacheKey);

        // 不区分大小写比较
        boolean match = cachedCode.equalsIgnoreCase(code.trim());
        if (!match) {
            log.warn("验证码不正确: captchaId={}", captchaId);
        }
        return match;
    }

    /**
     * 生成图片验证码
     *
     * @return CaptchaResult 包含 captchaId 和 base64 图片
     */
    private CaptchaResult generateImageCaptcha() {
        // 1. 生成随机验证码文本
        String code = generateRandomCode();

        // 2. 生成唯一 ID
        String captchaId = UUID.randomUUID().toString().replace("-", "");

        // 3. 将验证码存入 Redis（5 分钟过期）
        String cacheKey = CAPTCHA_PREFIX + captchaId;
        stringRedisTemplate.opsForValue().set(cacheKey, code, CAPTCHA_EXPIRE_MINUTES, TimeUnit.MINUTES);

        // 4. 生成图片并转为 base64
        String base64Image = generateCaptchaImage(code);

        return new CaptchaResult(captchaId, base64Image);
    }

    /**
     * 生成随机验证码文本
     */
    private String generateRandomCode() {
        StringBuilder sb = new StringBuilder(CAPTCHA_LENGTH);
        for (int i = 0; i < CAPTCHA_LENGTH; i++) {
            sb.append(CAPTCHA_CHARS.charAt(random.nextInt(CAPTCHA_CHARS.length())));
        }
        return sb.toString();
    }

    /**
     * 使用 Java Graphics2D 生成验证码图片
     *
     * @param code 验证码文本
     * @return Base64 编码的图片字符串
     */
    private String generateCaptchaImage(String code) {
        BufferedImage image = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // 设置背景
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);

        // 绘制干扰线
        g.setColor(new Color(200, 200, 200));
        for (int i = 0; i < 20; i++) {
            int x1 = random.nextInt(IMAGE_WIDTH);
            int y1 = random.nextInt(IMAGE_HEIGHT);
            int x2 = random.nextInt(IMAGE_WIDTH);
            int y2 = random.nextInt(IMAGE_HEIGHT);
            g.drawLine(x1, y1, x2, y2);
        }

        // 绘制干扰点
        g.setColor(new Color(150, 150, 150));
        for (int i = 0; i < 50; i++) {
            int x = random.nextInt(IMAGE_WIDTH);
            int y = random.nextInt(IMAGE_HEIGHT);
            g.drawOval(x, y, 1, 1);
        }

        // 绘制验证码文字
        Font font = new Font("Arial", Font.BOLD, 28);
        g.setFont(font);

        for (int i = 0; i < code.length(); i++) {
            // 随机颜色
            g.setColor(new Color(
                    random.nextInt(100),
                    random.nextInt(100),
                    random.nextInt(100)
            ));

            // 随机旋转角度
            double theta = Math.toRadians(random.nextInt(30) - 15);
            int x = 10 + i * 26;
            int y = 30;

            g.rotate(theta, x, y);
            g.drawString(String.valueOf(code.charAt(i)), x, y);
            g.rotate(-theta, x, y);
        }

        g.dispose();

        // 转为 Base64
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "PNG", baos);
            byte[] imageBytes = baos.toByteArray();
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            log.error("验证码图片生成失败", e);
            return "";
        }
    }

    /**
     * 验证码结果
     */
    public static class CaptchaResult {
        private final String captchaId;
        private final String imageBase64;

        public CaptchaResult(String captchaId, String imageBase64) {
            this.captchaId = captchaId;
            this.imageBase64 = imageBase64;
        }

        public String getCaptchaId() {
            return captchaId;
        }

        public String getImageBase64() {
            return imageBase64;
        }
    }
}
