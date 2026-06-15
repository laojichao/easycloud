package com.easycloud.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * 邮件发送服务 - 对应 PHP PHPMailer SMTP 发送
 * <p>
 * 配置项（yixi_config）：
 * - mail_host   SMTP 服务器
 * - mail_port   端口
 * - mail_user   用户名
 * - mail_pass   密码
 * - mail_from   发件人
 * <p>
 * 使用 Spring Boot 的 JavaMailSender 实现，通过 MailProperties 动态配置。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;
    private final ConfigService configService;

    /**
     * 异步发送 HTML 邮件
     *
     * @param to      收件人邮箱
     * @param subject 邮件主题
     * @param content 邮件内容（支持 HTML）
     */
    @Async
    public CompletableFuture<Boolean> sendMail(String to, String subject, String content) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String from = configService.getSetting("mail_from");
            if (from == null || from.isBlank()) {
                from = configService.getSetting("mail_user");
            }
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            javaMailSender.send(message);
            log.info("邮件发送成功: to={}, subject={}", to, subject);
            return CompletableFuture.completedFuture(true);
        } catch (MessagingException e) {
            log.error("邮件发送失败: to={}, subject={}, error={}", to, subject, e.getMessage(), e);
            return CompletableFuture.completedFuture(false);
        } catch (Exception e) {
            log.error("邮件发送异常: to={}, subject={}, error={}", to, subject, e.getMessage(), e);
            return CompletableFuture.completedFuture(false);
        }
    }

    /**
     * 同步发送邮件（用于需要确认发送结果的场景）
     *
     * @param to      收件人邮箱
     * @param subject 邮件主题
     * @param content 邮件内容（支持 HTML）
     * @return 是否发送成功
     */
    public boolean sendMailSync(String to, String subject, String content) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String from = configService.getSetting("mail_from");
            if (from == null || from.isBlank()) {
                from = configService.getSetting("mail_user");
            }
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            javaMailSender.send(message);
            log.info("邮件发送成功(同步): to={}, subject={}", to, subject);
            return true;
        } catch (Exception e) {
            log.error("邮件发送失败(同步): to={}, subject={}, error={}", to, subject, e.getMessage(), e);
            return false;
        }
    }
}
