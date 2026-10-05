package com.easycloud.service;

import com.easycloud.entity.User;
import com.easycloud.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 通知服务：站内事件触发邮件/短信通知
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final MailService mailService;
    private final SmsService smsService;
    private final UserMapper userMapper;

    public void sendEmailNotification(Long uid, String subject, String content) {
        if (uid == null) {
            return;
        }
        User user = userMapper.selectById(uid);
        if (user == null || !StringUtils.hasText(user.getEmail())) {
            return;
        }
        sendEmailNotification(user.getEmail(), subject, content);
    }

    public void sendEmailNotification(String email, String subject, String content) {
        if (!StringUtils.hasText(email)) {
            return;
        }
        mailService.sendMail(email, subject, content)
                .whenComplete((ok, e) -> {
                    if (e != null || !Boolean.TRUE.equals(ok)) {
                        log.warn("邮件通知发送失败 to={}", email);
                    }
                });
    }

    public void sendSmsNotification(String phone, String content, String scope) {
        if (!StringUtils.hasText(phone)) {
            return;
        }
        smsService.sendSms(phone, content, scope)
                .whenComplete((ok, e) -> {
                    if (e != null || !Boolean.TRUE.equals(ok)) {
                        log.warn("短信通知发送失败 phone={}", phone);
                    }
                });
    }

    public void sendSmsVerificationCode(String phone, String code) {
        smsService.sendSms(phone, code, "reg")
                .whenComplete((ok, e) -> {
                    if (e != null || !Boolean.TRUE.equals(ok)) {
                        log.warn("验证码短信发送失败 phone={}", phone);
                    }
                });
    }
}
