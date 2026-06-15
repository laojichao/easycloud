package com.easycloud.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.easycloud.entity.InviteLog;
import com.easycloud.mapper.InviteLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 邀请返利服务
 * <p>
 * 处理用户邀请好友后的返利记录管理，对应原 PHP 项目 yixi_invitelog 相关逻辑。
 * 当用户邀请新用户注册或消费时，邀请人可获得返利奖励。
 *
 * @author EasyCloud
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class InviteService {

    private final InviteLogMapper inviteLogMapper;

    /**
     * 添加邀请返利记录
     *
     * @param uid   邀请人用户ID
     * @param qq    被邀请人QQ
     * @param type  返利类型
     * @param money 返利金额
     * @param bz    备注
     * @return 创建的邀请记录
     */
    @Transactional
    public InviteLog addInviteLog(Long uid, String qq, String type, BigDecimal money, String bz) {
        InviteLog log = new InviteLog();
        log.setUid(uid);
        log.setQq(qq);
        log.setType(type);
        log.setMoney(money);
        log.setBz(bz);
        log.setCreationTime(LocalDateTime.now());
        inviteLogMapper.insert(log);
        return log;
    }

    /**
     * 获取用户的邀请记录列表
     */
    public Page<InviteLog> getList(Long uid, int page, int size) {
        LambdaQueryWrapper<InviteLog> wrapper = new LambdaQueryWrapper<InviteLog>()
                .eq(InviteLog::getUid, uid)
                .orderByDesc(InviteLog::getCreationTime);
        return inviteLogMapper.selectPage(new Page<>(page, size), wrapper);
    }
}
