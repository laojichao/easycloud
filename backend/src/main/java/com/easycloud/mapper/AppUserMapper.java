package com.easycloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easycloud.entity.AppUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 应用用户关联 Mapper
 * 对应实体 {@link AppUser}，表 yixi_appuser
 * 提供用户与应用绑定关系的 CRUD 操作
 */
@Mapper
public interface AppUserMapper extends BaseMapper<AppUser> {
}
