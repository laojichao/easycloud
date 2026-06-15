package com.easycloud.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easycloud.common.Md5Util;
import com.easycloud.entity.User;
import com.easycloud.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户服务
 * <p>
 * 处理用户注册、登录、信息管理等核心业务逻辑。
 * 对应原 PHP 项目中 yixi_user 相关的注册/登录/余额操作逻辑。
 * <ul>
 *   <li>注册：校验用户名唯一性 → MD5 加盐加密密码 → 插入数据库</li>
 *   <li>登录：查询用户 → 验证密码（MD5 加盐比对）</li>
 *   <li>余额：支持充值、扣减，使用事务保证原子性</li>
 * </ul>
 * <p>
 * 密码加密方式：md5(password + '!@#%!s!0')，与原 PHP 项目保持一致。
 *
 * @author EasyCloud
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    /**
     * 用户注册
     * 对应 PHP: 检查用户名唯一 → md5(pwd + salt) → INSERT
     *
     * @param username 用户名
     * @param password 原始密码
     * @param ip       注册IP
     * @param qq       QQ号（可为null）
     * @param email    邮箱（可为null）
     * @return 注册成功的用户
     */
    @Transactional
    public User register(String username, String password, String ip, String qq, String email) {
        // 校验用户名唯一
        Long existing = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getUser, username));
        if (existing > 0) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUser(username);
        user.setPwd(Md5Util.encryptPassword(password));
        user.setRmb(BigDecimal.ZERO);
        user.setIp(ip);
        user.setQq(qq);
        user.setEmail(email);
        user.setRegdate(LocalDateTime.now());
        userMapper.insert(user);
        return user;
    }

    /**
     * 用户登录
     * 对应 PHP: SELECT + md5 验证密码
     *
     * @param username 用户名
     * @param password 原始密码
     * @return 登录成功的用户，失败返回 null
     */
    public User login(String username, String password) {
        User user = getByUsername(username);
        if (user == null) {
            return null;
        }
        if (Md5Util.verifyPassword(password, user.getPwd())) {
            return user;
        }
        return null;
    }

    /**
     * 根据 uid 查找用户
     */
    public User getByUid(Long uid) {
        return userMapper.selectById(uid);
    }

    /**
     * 根据用户名查找用户
     */
    public User getByUsername(String username) {
        return userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUser, username));
    }

    /**
     * 原子更新用户余额（防止并发竞态条件）
     * <p>
     * 使用 SQL 层面的原子操作 UPDATE ... SET rmb = rmb + amount，
     * 避免 SELECT-then-UPDATE 的 TOCTOU 竞态条件。
     * 在并发场景下（如同时提现、同时支付回调），保证余额不会出现负数或丢失更新。
     *
     * @param uid    用户ID
     * @param amount 变动金额（正数增加，负数减少）
     * @throws RuntimeException 用户不存在或余额不足
     */
    @Transactional
    public void updateRmb(Long uid, BigDecimal amount) {
        int affected = userMapper.updateRmbAtomic(uid, amount);
        if (affected == 0) {
            // 可能是用户不存在或余额不足
            User user = userMapper.selectById(uid);
            if (user == null) {
                throw new RuntimeException("用户不存在");
            }
            throw new RuntimeException("余额不足");
        }
    }

    /**
     * 更新用户信息
     */
    @Transactional
    public void updateUser(User user) {
        userMapper.updateById(user);
    }

    /**
     * 删除用户
     */
    @Transactional
    public void deleteUser(Long uid) {
        userMapper.deleteById(uid);
    }
}
