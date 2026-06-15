package com.easycloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easycloud.entity.UserJk;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户接口关联 Mapper
 * 对应实体 {@link UserJk}，表 yixi_userjk
 * 提供用户 API 接口权限的 CRUD 操作
 */
@Mapper
public interface UserJkMapper extends BaseMapper<UserJk> {
}
