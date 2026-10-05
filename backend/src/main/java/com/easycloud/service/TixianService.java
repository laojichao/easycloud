package com.easycloud.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.easycloud.entity.Tixian;
import com.easycloud.mapper.TixianMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 提现服务 - 对应 PHP yixi_tixian（申请冻结余额，审核通过/拒绝）
 */
@Service
@RequiredArgsConstructor
public class TixianService {

    private final TixianMapper tixianMapper;
    private final UserService userService;

    @Transactional
    public Tixian apply(Long uid, String account, String name, BigDecimal money, String type) {
        if (account == null || account.isBlank() || name == null || name.isBlank()) {
            throw new RuntimeException("请填写完整的收款信息");
        }
        if (money == null || money.compareTo(BigDecimal.ONE) < 0) {
            throw new RuntimeException("最低提现金额为一元");
        }
        Tixian tixian = new Tixian();
        tixian.setUid(uid);
        tixian.setAccount(account);
        tixian.setName(name);
        tixian.setMoney(money);
        tixian.setStatus(Tixian.STATUS_PENDING);
        tixian.setType(type);
        tixian.setAddtime(LocalDateTime.now());
        // 先冻结余额，再落库
        userService.updateRmb(uid, money.negate());
        tixianMapper.insert(tixian);
        return tixian;
    }

    public Page<Tixian> getList(Long uid, int page, int size) {
        return tixianMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<Tixian>()
                        .eq(Tixian::getUid, uid)
                        .orderByDesc(Tixian::getAddtime));
    }

    public Page<Tixian> getAll(int page, int size, Integer status) {
        LambdaQueryWrapper<Tixian> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Tixian::getStatus, status);
        }
        wrapper.orderByDesc(Tixian::getAddtime);
        return tixianMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Transactional
    public void approve(Long id, BigDecimal realmoney) {
        Tixian tixian = tixianMapper.selectById(id);
        if (tixian == null) {
            throw new RuntimeException("提现记录不存在");
        }
        int affected = tixianMapper.reviewPending(id, Tixian.STATUS_APPROVED, realmoney, LocalDateTime.now());
        if (affected == 0) {
            throw new RuntimeException("记录状态已变化，请刷新后重试");
        }
    }

    @Transactional
    public void reject(Long id) {
        Tixian tixian = tixianMapper.selectById(id);
        if (tixian == null) {
            throw new RuntimeException("提现记录不存在");
        }
        int affected = tixianMapper.reviewPending(id, Tixian.STATUS_REJECTED, null, LocalDateTime.now());
        if (affected == 0) {
            throw new RuntimeException("记录状态已变化，请刷新后重试");
        }
        // 拒绝后退回冻结金额
        userService.updateRmb(tixian.getUid(), tixian.getMoney());
    }
}
