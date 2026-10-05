package com.easycloud.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.Properties;
import java.util.concurrent.CompletableFuture;

/**
 * 邮件服务 - 对应 PHP send_mail()（SMTP 配置存储在 yixi_config）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final ConfigService configService;

    public CompletableFuture<Boolean> sendMail(String to, String subject, String content) {
        return CompletableFuture.supplyAsync(() -> sendMailSync(to, subject, content));
    }

    public boolean sendMailSync(String to, String subject, String content) {
        try {
            String from = configService.getSetting("mail_name");
            String host = configService.getSetting("mail_smtp");
            String password = configService.getSetting("mail_pwd");
            String portStr = configService.getSetting("mail_port");
            if (isBlank(from) || isBlank(host) || isBlank(password)) {
                log.warn("邮件配置不完整，跳过发送");
                return false;
            }
            int port = 25;
            try {
                if (portStr != null && !portStr.isBlank()) {
                    port = Integer.parseInt(portStr);
                }
            } catch (NumberFormatException ignored) {
            }

            JavaMailSender sender = mailSenderProvider.getIfAvailable();
            JavaMailSenderImpl impl;
            if (sender instanceof JavaMailSenderImpl) {
                impl = (JavaMailSenderImpl) sender;
                impl.setHost(host);
                impl.setPort(port);
                impl.setUsername(from);
                impl.setPassword(password);
                impl.setJavaMailProperties(buildProperties(from, password, port));
            } else {
                impl = new JavaMailSenderImpl();
                impl.setHost(host);
                impl.setPort(port);
                impl.setUsername(from);
                impl.setPassword(password);
                impl.setJavaMailProperties(buildProperties(from, password, port));
            }

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            // 通过 MailSender 接口发送，避免编译期依赖 jakarta.mail 的 MimeMessage 重载
            org.springframework.mail.MailSender mailSender = impl;
            mailSender.send(message);
            return true;
        } catch (Exception e) {
            log.error("邮件发送失败 to={}: {}", to, e.getMessage());
            return false;
        }
    }

    private Properties buildProperties(String username, String password, int port) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", String.valueOf(port == 587));
        props.put("mail.smtp.ssl.enable", String.valueOf(port == 465));
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "15000");
        props.put("mail.smtp.writetimeout", "15000");
        return props;
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
