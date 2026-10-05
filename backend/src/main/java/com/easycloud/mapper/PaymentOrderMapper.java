package com.easycloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easycloud.entity.PaymentOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;

/**
 * PaymentOrder Mapper
 */
@Mapper
public interface PaymentOrderMapper extends BaseMapper<PaymentOrder> {
    @Update("UPDATE yixi_payment_order SET status = 'paid', trade_no = #{tradeNo}, pay_time = #{payTime} WHERE order_no = #{orderNo} AND status IN ('pending', 'failed')")
    int markAsPaid(@Param("orderNo") String orderNo, @Param("tradeNo") String tradeNo, @Param("payTime") LocalDateTime payTime);

    @Update("UPDATE yixi_payment_order SET status = 'refunded' WHERE order_no = #{orderNo} AND status = 'paid'")
    int markRefunded(@Param("orderNo") String orderNo);
}
