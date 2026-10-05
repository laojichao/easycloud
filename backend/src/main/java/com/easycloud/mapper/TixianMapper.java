package com.easycloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easycloud.entity.Tixian;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Tixian Mapper
 */
@Mapper
public interface TixianMapper extends BaseMapper<Tixian> {
    @Update("UPDATE yixi_tixian SET status = #{status}, realmoney = #{realmoney}, endtime = #{endtime} WHERE id = #{id} AND status = 0")
    int reviewPending(@Param("id") Long id, @Param("status") int status, @Param("realmoney") BigDecimal realmoney, @Param("endtime") LocalDateTime endtime);
}
