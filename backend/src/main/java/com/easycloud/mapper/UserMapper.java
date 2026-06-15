package com.easycloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easycloud.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;

/**
 * 用户 Mapper
 * 对应实体 {@link User}，表 yixi_user
 * 提供用户信息的 CRUD 操作
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 原子更新用户余额（防止并发竞态条件）
     * 使用 SQL 层面的 rmb + amount 计算，避免 SELECT-then-UPDATE 竞态
     *
     * @param uid    用户ID
     * @param amount 变动金额（正数增加，负数减少）
     * @return 受影响行数（0 表示余额不足或用户不存在）
     */
    @Update("UPDATE yixi_user SET rmb = rmb + #{amount} WHERE uid = #{uid} AND rmb + #{amount} >= 0")
    int updateRmbAtomic(@Param("uid") Long uid, @Param("amount") BigDecimal amount);
}
