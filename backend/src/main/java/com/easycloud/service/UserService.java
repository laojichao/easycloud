package com.easycloud.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easycloud.common.Md5Util;
import com.easycloud.entity.Checkin;
import com.easycloud.entity.InviteLog;
import com.easycloud.entity.PaymentOrder;
import com.easycloud.entity.Point;
import com.easycloud.entity.Tixian;
import com.easycloud.entity.User;
import com.easycloud.entity.UserJk;
import com.easycloud.entity.WorkOrder;
import com.easycloud.mapper.CheckinMapper;
import com.easycloud.mapper.InviteLogMapper;
import com.easycloud.mapper.PaymentOrderMapper;
import com.easycloud.mapper.PointMapper;
import com.easycloud.mapper.TixianMapper;
import com.easycloud.mapper.UserJkMapper;
import com.easycloud.mapper.UserMapper;
import com.easycloud.mapper.WorkOrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;

/**
 * 用户服务 - 对应 PHP 用户中心（yixi_user）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private static final String INVITE_CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int INVITE_CODE_LENGTH = 8;

    private final UserMapper userMapper;
    private final PointMapper pointMapper;
    private final CheckinMapper checkinMapper;
    private final TixianMapper tixianMapper;
    private final PaymentOrderMapper paymentOrderMapper;
    private final WorkOrderMapper workOrderMapper;
    private final InviteLogMapper inviteLogMapper;
    private final UserJkMapper userJkMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final SecureRandom random = new SecureRandom();

    @Transactional
    public User register(String username, String password, String qq, String email, String inviteCode) {
        if (!StringUtils.hasText(username) || username.length() < 3 || username.length() > 32) {
            throw new RuntimeException("用户名长度需在3-32位之间");
        }
        if (!StringUtils.hasText(password) || password.length() < 6) {
            throw new RuntimeException("密码至少6位");
        }
        Long exists = userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getUser, username));
        if (exists != null && exists > 0) {
            throw new RuntimeException("用户名已存在");
        }
        // 邀请码（选填）：填写时必须是有效邀请人的邀请码
        if (StringUtils.hasText(inviteCode) && getByInviteCode(inviteCode) == null) {
            throw new RuntimeException("邀请码无效");
        }
        User user = new User();
        user.setUser(username);
        user.setPwd(passwordEncoder.encode(password));
        user.setQq(qq);
        user.setEmail(email);
        user.setRmb(java.math.BigDecimal.ZERO);
        user.setRegdate(LocalDateTime.now());
        userMapper.insert(user);
        ensureInviteCode(user);
        log.info("新用户注册: {} uid={}", username, user.getUid());
        return user;
    }

    public User login(String username, String password) {
        User user = getByUsername(username);
        if (user == null || !StringUtils.hasText(password)) {
            throw new RuntimeException("用户名或密码错误");
        }
        String stored = user.getPwd();
        if (passwordEncoder.matches(password, stored)) {
            return user;
        }
        // 兼容旧数据：PHP 明文 / MD5 加盐存储，验证通过后迁移为 BCrypt
        if (Md5Util.verifyPassword(password, stored)) {
            migrateToBcrypt(user, password);
            return user;
        }
        throw new RuntimeException("用户名或密码错误");
    }

    private void migrateToBcrypt(User user, String rawPassword) {
        try {
            User update = new User();
            update.setUid(user.getUid());
            update.setPwd(passwordEncoder.encode(rawPassword));
            userMapper.updateById(update);
            user.setPwd(update.getPwd());
        } catch (Exception e) {
            log.warn("密码迁移失败 uid={}: {}", user.getUid(), e.getMessage());
        }
    }

    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    private String generateUniqueInviteCode() {
        for (int i = 0; i < 32; i++) {
            StringBuilder sb = new StringBuilder("U");
            for (int j = 0; j < INVITE_CODE_LENGTH; j++) {
                sb.append(INVITE_CODE_CHARS.charAt(random.nextInt(INVITE_CODE_CHARS.length())));
            }
            String code = sb.toString();
            if (getByInviteCode(code) == null) {
                return code;
            }
        }
        throw new RuntimeException("邀请码生成失败，请重试");
    }

    @Transactional
    public String ensureInviteCode(User user) {
        if (StringUtils.hasText(user.getInvitecode())) {
            return user.getInvitecode();
        }
        String code = generateUniqueInviteCode();
        User update = new User();
        update.setUid(user.getUid());
        update.setInvitecode(code);
        userMapper.updateById(update);
        user.setInvitecode(code);
        return code;
    }

    public User getByInviteCode(String inviteCode) {
        if (!StringUtils.hasText(inviteCode)) {
            return null;
        }
        return userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getInvitecode, inviteCode).last("LIMIT 1"));
    }

    public User getByUid(Long uid) {
        return userMapper.selectById(uid);
    }

    public User getByUsername(String username) {
        if (!StringUtils.hasText(username)) {
            return null;
        }
        return userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUser, username).last("LIMIT 1"));
    }

    @Transactional
    public void updateRmb(Long uid, BigDecimal amount) {
        int affected = userMapper.updateRmbAtomic(uid, amount);
        if (affected == 0) {
            throw new RuntimeException("余额不足或用户不存在");
        }
    }

    @Transactional
    public void updateUser(User user) {
        userMapper.updateById(user);
    }

    /** 删除用户及其全部关联数据 */
    @Transactional
    public void deleteUser(Long uid) {
        pointMapper.delete(new LambdaQueryWrapper<Point>().eq(Point::getUid, uid));
        checkinMapper.delete(new LambdaQueryWrapper<Checkin>().eq(Checkin::getUid, uid));
        tixianMapper.delete(new LambdaQueryWrapper<Tixian>().eq(Tixian::getUid, uid));
        paymentOrderMapper.delete(new LambdaQueryWrapper<PaymentOrder>().eq(PaymentOrder::getUid, uid));
        workOrderMapper.delete(new LambdaQueryWrapper<WorkOrder>().eq(WorkOrder::getUid, uid));
        inviteLogMapper.delete(new LambdaQueryWrapper<InviteLog>().eq(InviteLog::getUid, uid));
        userJkMapper.delete(new LambdaQueryWrapper<UserJk>().eq(UserJk::getUid, uid));
        userMapper.deleteById(uid);
    }
}
