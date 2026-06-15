package com.easycloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easycloud.entity.PaymentOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;

/**
 * 支付订单 Mapper
 * 对应实体 {@link PaymentOrder}，表 yixi_payment_order
 * 提供支付订单的 CRUD 操作
 */
@Mapper
public interface PaymentOrderMapper extends BaseMapper<PaymentOrder> {

    /**
     * 原子标记订单为已支付（防止支付回调并发重复处理）
     * 仅当订单状态为 pending 时才更新，保证幂等性
     *
     * @param orderNo   订单号
     * @param tradeNo   交易号
     * @param payTime   支付时间
     * @return 受影响行数（0 表示订单已处理过）
     */
    @Update("UPDATE yixi_payment_order SET status = 'paid', trade_no = #{tradeNo}, pay_time = #{payTime} " +
            "WHERE order_no = #{orderNo} AND status = 'pending'")
    int markAsPaid(@Param("orderNo") String orderNo, @Param("tradeNo") String tradeNo, @Param("payTime") LocalDateTime payTime);
}
