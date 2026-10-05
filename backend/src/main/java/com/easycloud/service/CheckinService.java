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
 * 签到服务 - 对应 PHP yixi_qiandao 每日签到奖励
 */
@Service
@RequiredArgsConstructor
public class CheckinService {

    private final CheckinMapper checkinMapper;
    private final UserService userService;
    private final ConfigService configService;

    @Transactional
    public BigDecimal checkin(Long uid) {
        if (getTodayCheckin(uid)) {
            throw new RuntimeException("今日已签到");
        }
        BigDecimal reward;
        try {
            reward = new BigDecimal(configService.getSetting("checkin_reward"));
        } catch (Exception e) {
            reward = new BigDecimal("0.1");
        }
        Checkin checkin = new Checkin();
        checkin.setUid(uid);
        checkin.setDate(LocalDate.now());
        checkin.setReward(reward);
        checkin.setAddtime(LocalDateTime.now());
        checkinMapper.insert(checkin);
        userService.updateRmb(uid, reward);
        return reward;
    }

    public Page<Checkin> getCheckinList(Long uid, int page, int size) {
        return checkinMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<Checkin>()
                        .eq(Checkin::getUid, uid)
                        .orderByDesc(Checkin::getAddtime));
    }

    public boolean getTodayCheckin(Long uid) {
        Long count = checkinMapper.selectCount(new LambdaQueryWrapper<Checkin>()
                .eq(Checkin::getUid, uid)
                .eq(Checkin::getDate, LocalDate.now()));
        return count != null && count > 0;
    }
}
