package com.easycloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easycloud.entity.Point;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

/**
 * 积分记录 Mapper
 * 对应实体 {@link Point}，表 yixi_points
 * 提供积分收支记录的 CRUD 操作及积分汇总查询
 */
@Mapper
public interface PointMapper extends BaseMapper<Point> {

    /**
     * 查询用户积分总和
     *
     * @param uid 用户ID
     * @return 积分总和，无记录返回 0
     */
    @Select("SELECT COALESCE(SUM(point), 0) FROM yixi_points WHERE uid = #{uid}")
    BigDecimal sumPointByUid(@Param("uid") Long uid);
}
