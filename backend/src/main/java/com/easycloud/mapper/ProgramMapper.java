package com.easycloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easycloud.entity.Program;
import org.apache.ibatis.annotations.Mapper;

/**
 * 程序/产品 Mapper
 * 对应实体 {@link Program}，表 yixi_program
 * 提供软件产品信息的 CRUD 操作
 */
@Mapper
public interface ProgramMapper extends BaseMapper<Program> {
}
