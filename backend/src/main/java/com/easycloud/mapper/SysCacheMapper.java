package com.easycloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easycloud.entity.SysCache;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统缓存 Mapper
 * 对应实体 {@link SysCache}，表 yixi_cache
 * 提供系统级 K-V 缓存的 CRUD 操作
 */
@Mapper
public interface SysCacheMapper extends BaseMapper<SysCache> {
}
