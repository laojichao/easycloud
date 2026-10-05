package com.easycloud.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.easycloud.entity.InviteLog;
import com.easycloud.mapper.InviteLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 邀请返利服务 - 对应 PHP yixi_invitelog
 */
@Service
@RequiredArgsConstructor
public class InviteService {

    private final InviteLogMapper inviteLogMapper;

    public InviteLog addInviteLog(Long uid, String qq, String type, BigDecimal money, String bz) {
        InviteLog record = new InviteLog();
        record.setUid(uid);
        record.setQq(qq);
        record.setType(type);
        record.setMoney(money);
        record.setBz(bz);
        record.setCreationTime(LocalDateTime.now());
        inviteLogMapper.insert(record);
        return record;
    }

    public Page<InviteLog> getList(Long uid, int page, int size) {
        return inviteLogMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<InviteLog>()
                        .eq(InviteLog::getUid, uid)
                        .orderByDesc(InviteLog::getCreationTime));
    }
}
