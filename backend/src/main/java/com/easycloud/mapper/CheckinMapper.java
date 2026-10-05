package com.easycloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easycloud.entity.Checkin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

/**
 * Checkin Mapper
 */
@Mapper
public interface CheckinMapper extends BaseMapper<Checkin> {
    @Select("SELECT COALESCE(SUM(reward), 0) FROM yixi_qiandao")
    BigDecimal sumAllReward();
}
