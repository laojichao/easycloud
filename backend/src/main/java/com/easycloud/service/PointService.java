package com.easycloud.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.easycloud.entity.Point;
import com.easycloud.mapper.PointMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 积分服务 - 对应 PHP yixi_points
 */
@Service
@RequiredArgsConstructor
public class PointService {

    private final PointMapper pointMapper;

    public Point addPoint(Long uid, BigDecimal point, String orderId, String action) {
        Point record = new Point();
        record.setUid(uid);
        record.setPoint(point);
        record.setOrderId(orderId);
        record.setAction(action);
        record.setAddtime(LocalDateTime.now());
        pointMapper.insert(record);
        return record;
    }

    public Page<Point> getList(Long uid, int page, int size) {
        return pointMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<Point>()
                        .eq(Point::getUid, uid)
                        .orderByDesc(Point::getAddtime));
    }

    public BigDecimal getTotal(Long uid) {
        return pointMapper.sumPointByUid(uid);
    }
}
