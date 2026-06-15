package com.easycloud.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.easycloud.entity.Tixian;
import com.easycloud.mapper.TixianMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 提现服务
 * <p>
 * 处理用户提现申请的创建、审核和拒绝等业务逻辑，对应原 PHP 项目 yixi_tixian 相关逻辑。
 * 提现流程：用户申请（扣减余额） → 管理员审核 → 批准（打款）/ 拒绝（退回余额）
 * <p>
 * 状态流转：待处理(0) → 已处理(1) 或 已拒绝(2)
 *
 * @author EasyCloud
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class TixianService {

    private final TixianMapper tixianMapper;
    private final UserService userService;

    /**
     * 申请提现
     * 检查余额是否充足，扣减余额后创建提现记录
     *
     * @param uid     用户ID
     * @param account 提现账号
     * @param name    姓名
     * @param money   提现金额
     * @param type    提现方式
     * @return 创建的提现记录
     */
    @Transactional
    public Tixian apply(Long uid, String account, String name, BigDecimal money, String type) {
        if (!StringUtils.hasText(account)) {
            throw new RuntimeException("提现账号不能为空");
        }
        if (!StringUtils.hasText(name)) {
            throw new RuntimeException("姓名不能为空");
        }
        if (money == null || money.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("提现金额必须大于0");
        }

        // 检查余额并扣减（UserService.updateRmb 内部会检查余额充足性）
        userService.updateRmb(uid, money.negate());

        // 创建提现记录
        Tixian tixian = new Tixian();
        tixian.setUid(uid);
        tixian.setAccount(account);
        tixian.setName(name);
        tixian.setMoney(money);
        tixian.setStatus(0); // 待处理
        tixian.setType(type);
        tixian.setAddtime(LocalDateTime.now());
        tixianMapper.insert(tixian);
        return tixian;
    }

    /**
     * 获取用户的提现记录列表
     */
    public Page<Tixian> getList(Long uid, int page, int size) {
        LambdaQueryWrapper<Tixian> wrapper = new LambdaQueryWrapper<Tixian>()
                .eq(Tixian::getUid, uid)
                .orderByDesc(Tixian::getAddtime);
        return tixianMapper.selectPage(new Page<>(page, size), wrapper);
    }

    /**
     * 获取所有提现记录列表（管理员）
     *
     * @param page   页码
     * @param size   每页条数
     * @param status 状态筛选，null 表示全部
     */
    public Page<Tixian> getAll(int page, int size, Integer status) {
        LambdaQueryWrapper<Tixian> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Tixian::getStatus, status);
        }
        wrapper.orderByDesc(Tixian::getAddtime);
        return tixianMapper.selectPage(new Page<>(page, size), wrapper);
    }

    /**
     * 批准提现（管理员）
     * 更新状态为已处理，设置实际到账金额
     *
     * @param id       提现记录ID
     * @param realmoney 实际到账金额
     */
    @Transactional
    public void approve(Long id, BigDecimal realmoney) {
        Tixian tixian = tixianMapper.selectById(id);
        if (tixian == null) {
            throw new RuntimeException("提现记录不存在");
        }
        if (tixian.getStatus() != 0) {
            throw new RuntimeException("该提现记录已处理");
        }
        tixian.setRealmoney(realmoney);
        tixian.setStatus(1); // 已处理
        tixian.setEndtime(LocalDateTime.now());
        tixianMapper.updateById(tixian);
    }

    /**
     * 拒绝提现（管理员）
     * 更新状态为已拒绝，退回用户余额
     *
     * @param id 提现记录ID
     */
    @Transactional
    public void reject(Long id) {
        Tixian tixian = tixianMapper.selectById(id);
        if (tixian == null) {
            throw new RuntimeException("提现记录不存在");
        }
        if (tixian.getStatus() != 0) {
            throw new RuntimeException("该提现记录已处理");
        }
        // 退回用户余额
        userService.updateRmb(tixian.getUid(), tixian.getMoney());
        tixian.setStatus(2); // 已拒绝
        tixian.setEndtime(LocalDateTime.now());
        tixianMapper.updateById(tixian);
    }
}
