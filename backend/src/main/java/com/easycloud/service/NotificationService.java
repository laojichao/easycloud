package com.easycloud.service;

import com.easycloud.entity.User;
import com.easycloud.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 通知服务 - 整合邮件和短信发送
 * <p>
 * 提供统一的通知入口：
 * - 邮件通知：通过 MailService 发送
 * - 短信通知：通过 SmsService 发送
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final MailService mailService;
    private final SmsService smsService;
    private final UserMapper userMapper;

    /**
     * 发送邮件通知给指定用户
     *
     * @param userId  用户 ID
     * @param title   邮件标题
     * @param content 邮件内容（支持 HTML）
     */
    @Async
    public void sendEmailNotification(Long userId, String title, String content) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            log.warn("发送邮件通知失败：用户不存在, userId={}", userId);
            return;
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.warn("发送邮件通知失败：用户未绑定邮箱, userId={}", userId);
            return;
        }

        log.info("发送邮件通知: userId={}, email={}, title={}", userId, user.getEmail(), title);
        mailService.sendMail(user.getEmail(), title, content);
    }

    /**
     * 发送邮件通知给指定邮箱
     *
     * @param email   收件人邮箱
     * @param title   邮件标题
     * @param content 邮件内容（支持 HTML）
     */
    @Async
    public void sendEmailNotification(String email, String title, String content) {
        if (email == null || email.isBlank()) {
            log.warn("发送邮件通知失败：邮箱为空");
            return;
        }
        log.info("发送邮件通知: email={}, title={}", email, title);
        mailService.sendMail(email, title, content);
    }

    /**
     * 发送短信通知
     *
     * @param phone   手机号
     * @param content 短信内容（验证码）
     * @param scope   短信用途（如 login, register, reset_pwd）
     */
    @Async
    public void sendSmsNotification(String phone, String content, String scope) {
        if (phone == null || phone.isBlank()) {
            log.warn("发送短信通知失败：手机号为空");
            return;
        }
        log.info("发送短信通知: phone={}, scope={}", phone, scope);
        smsService.sendSms(phone, content, scope);
    }

    /**
     * 发送短信验证码
     *
     * @param phone 手机号
     * @param code  验证码
     */
    @Async
    public void sendSmsVerificationCode(String phone, String code) {
        sendSmsNotification(phone, code, "verification");
    }
}
