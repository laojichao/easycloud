package com.easycloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easycloud.entity.SysConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统配置 Mapper
 * 对应实体 {@link SysConfig}，表 yixi_config
 * 提供系统配置项的 CRUD 操作，配合 ConfigService 实现 Redis 缓存
 */
@Mapper
public interface SysConfigMapper extends BaseMapper<SysConfig> {
}
