package com.easycloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easycloud.entity.Sig;
import org.apache.ibatis.annotations.Mapper;

/**
 * 应用签名/安全配置 Mapper
 * 对应实体 {@link Sig}，表 yixi_sig
 * 提供应用安全签名配置的 CRUD 操作
 */
@Mapper
public interface SigMapper extends BaseMapper<Sig> {
}
