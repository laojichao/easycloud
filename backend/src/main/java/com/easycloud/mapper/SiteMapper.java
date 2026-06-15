package com.easycloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easycloud.entity.Site;
import org.apache.ibatis.annotations.Mapper;

/**
 * 站点/分站 Mapper
 * 对应实体 {@link Site}，表 yixi_site
 * 提供分站信息的 CRUD 操作
 */
@Mapper
public interface SiteMapper extends BaseMapper<Site> {
}
