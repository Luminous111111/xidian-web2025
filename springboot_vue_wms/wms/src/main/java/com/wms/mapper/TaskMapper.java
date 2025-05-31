package com.wms.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.wms.entity.Task;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 任务表 Mapper 接口
 * </p>
 *
 * @author wms
 * @since 2025-05-08
 */
@Mapper
public interface TaskMapper extends BaseMapper<Task> {
    IPage pageCC(IPage<Task> page, @Param(Constants.WRAPPER) Wrapper wrapper);

}
