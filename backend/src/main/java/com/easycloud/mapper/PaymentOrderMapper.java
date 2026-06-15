package com.easycloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easycloud.entity.PaymentOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付订单 Mapper
 * 对应实体 {@link PaymentOrder}，表 yixi_payment_order
 * 提供支付订单的 CRUD 操作
 */
@Mapper
public interface PaymentOrderMapper extends BaseMapper<PaymentOrder> {
}
