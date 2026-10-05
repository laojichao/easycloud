package com.easycloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easycloud.entity.Point;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

/**
 * Point Mapper
 */
@Mapper
public interface PointMapper extends BaseMapper<Point> {
    @Select("SELECT COALESCE(SUM(point), 0) FROM yixi_points WHERE uid = #{uid}")
    BigDecimal sumPointByUid(@Param("uid") Long uid);
}
