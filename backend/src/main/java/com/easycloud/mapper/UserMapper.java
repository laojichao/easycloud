package com.easycloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easycloud.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 Mapper
 * 对应实体 {@link User}，表 yixi_user
 * 提供用户信息的 CRUD 操作
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
