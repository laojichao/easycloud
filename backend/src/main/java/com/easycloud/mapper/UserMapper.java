package com.easycloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easycloud.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;

/**
 * User Mapper
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Update("UPDATE yixi_user SET rmb = rmb + #{amount} WHERE uid = #{uid} AND rmb + #{amount} >= 0")
    int updateRmbAtomic(@Param("uid") Long uid, @Param("amount") BigDecimal amount);
}
