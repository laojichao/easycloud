package com.easycloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easycloud.entity.App;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 应用 Mapper
 * 对应实体 {@link App}，表 yixi_apps
 * 提供应用信息的 CRUD 操作及自定义更新方法
 */
@Mapper
public interface AppMapper extends BaseMapper<App> {

    /**
     * 更新应用调用次数
     */
    @Update("UPDATE yixi_apps SET total = total + 1 WHERE id = #{appId}")
    int updateTotal(@Param("appId") Long appId);
}
