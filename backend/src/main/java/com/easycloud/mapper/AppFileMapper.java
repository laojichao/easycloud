package com.easycloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easycloud.entity.AppFile;
import org.apache.ibatis.annotations.Mapper;

/**
 * 应用文件 Mapper
 * 对应实体 {@link AppFile}，表 yixi_appfile
 * 提供应用关联文件的 CRUD 操作
 */
@Mapper
public interface AppFileMapper extends BaseMapper<AppFile> {
}
