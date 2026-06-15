package com.easycloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easycloud.entity.Tixian;
import org.apache.ibatis.annotations.Mapper;

/**
 * 提现记录 Mapper
 * 对应实体 {@link Tixian}，表 yixi_tixian
 * 提供用户提现申请的 CRUD 操作
 */
@Mapper
public interface TixianMapper extends BaseMapper<Tixian> {
}
