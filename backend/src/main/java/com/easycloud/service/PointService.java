package com.easycloud.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.easycloud.entity.Point;
import com.easycloud.mapper.PointMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 积分服务
 * <p>
 * 处理用户积分/佣金的记录管理和查询，对应原 PHP 项目 yixi_points 相关逻辑。
 * 积分来源包括签到奖励、邀请返利、消费返佣等，可查询总积分和积分明细。
 *
 * @author EasyCloud
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class PointService {

    private final PointMapper pointMapper;

    /**
     * 添加积分记录
     *
     * @param uid     用户ID
     * @param point   积分数
     * @param orderId 关联订单号
     * @param action  行为类型
     * @return 创建的积分记录
     */
    @Transactional
    public Point addPoint(Long uid, BigDecimal point, String orderId, String action) {
        Point p = new Point();
        p.setUid(uid);
        p.setPoint(point);
        p.setOrderId(orderId);
        p.setAction(action);
        p.setAddtime(LocalDateTime.now());
        pointMapper.insert(p);
        return p;
    }

    /**
     * 获取用户的积分记录列表
     */
    public Page<Point> getList(Long uid, int page, int size) {
        LambdaQueryWrapper<Point> wrapper = new LambdaQueryWrapper<Point>()
                .eq(Point::getUid, uid)
                .orderByDesc(Point::getAddtime);
        return pointMapper.selectPage(new Page<>(page, size), wrapper);
    }

    /**
     * 获取用户总积分
     */
    public BigDecimal getTotal(Long uid) {
        return pointMapper.sumPointByUid(uid);
    }
}
