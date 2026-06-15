package com.easycloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easycloud.entity.Checkin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

/**
 * 签到记录 Mapper
 * 对应实体 {@link Checkin}，表 yixi_qiandao
 * 提供用户签到记录的 CRUD 操作及聚合查询
 */
@Mapper
public interface CheckinMapper extends BaseMapper<Checkin> {

    /**
     * 查询所有签到记录的奖励总和
     *
     * @return 签到总奖励金额，无记录返回 0
     */
    @Select("SELECT COALESCE(SUM(reward), 0) FROM yixi_qiandao")
    BigDecimal sumAllReward();
}
