package com.easycloud.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.easycloud.entity.Checkin;
import com.easycloud.mapper.CheckinMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 签到服务
 * <p>
 * 处理用户每日签到逻辑，对应原 PHP 项目 yixi_qiandao 相关逻辑。
 * 签到规则：每个用户每天只能签到一次，签到后获得配置金额的余额奖励。
 * <p>
 * 核心流程：检查当日是否已签到 → 读取奖励金额配置 → 插入签到记录 → 更新用户余额
 *
 * @author EasyCloud
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class CheckinService {

    private final CheckinMapper checkinMapper;
    private final UserService userService;
    private final ConfigService configService;

    /**
     * 用户签到
     * 检查今天是否已签到，未签到则插入记录并更新用户余额
     *
     * @param uid 用户ID
     * @return 签到获得的奖励金额
     */
    @Transactional
    public BigDecimal checkin(Long uid) {
        LocalDate today = LocalDate.now();

        // 检查今天是否已签到
        Long count = checkinMapper.selectCount(
                new LambdaQueryWrapper<Checkin>()
                        .eq(Checkin::getUid, uid)
                        .eq(Checkin::getDate, today));
        if (count > 0) {
            throw new RuntimeException("今天已签到");
        }

        // 从配置读取签到奖励金额，默认 0.1
        String rewardStr = configService.getSetting("checkin_reward");
        BigDecimal reward;
        try {
            reward = rewardStr != null ? new BigDecimal(rewardStr) : new BigDecimal("0.1");
        } catch (NumberFormatException e) {
            reward = new BigDecimal("0.1");
        }

        // 插入签到记录
        Checkin checkin = new Checkin();
        checkin.setUid(uid);
        checkin.setDate(today);
        checkin.setReward(reward);
        checkin.setAddtime(LocalDateTime.now());
        try {
            checkinMapper.insert(checkin);
        } catch (Exception e) {
            // 并发场景下可能因唯一约束冲突导致插入失败，说明已签到
            throw new RuntimeException("今天已签到");
        }

        // 更新用户余额
        userService.updateRmb(uid, reward);

        return reward;
    }

    /**
     * 获取用户签到历史列表
     */
    public Page<Checkin> getCheckinList(Long uid, int page, int size) {
        LambdaQueryWrapper<Checkin> wrapper = new LambdaQueryWrapper<Checkin>()
                .eq(Checkin::getUid, uid)
                .orderByDesc(Checkin::getAddtime);
        return checkinMapper.selectPage(new Page<>(page, size), wrapper);
    }

    /**
     * 查询今天是否已签到
     *
     * @param uid 用户ID
     * @return 今天是否已签到
     */
    public boolean getTodayCheckin(Long uid) {
        LocalDate today = LocalDate.now();
        Long count = checkinMapper.selectCount(
                new LambdaQueryWrapper<Checkin>()
                        .eq(Checkin::getUid, uid)
                        .eq(Checkin::getDate, today));
        return count > 0;
    }
}
