package com.wms.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wms.entity.Task;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 任务表 服务类
 * </p>
 *
 * @author wms
 * @since 2025-05-08
 */
public interface TaskService extends IService<Task> {
    IPage pageCC(IPage<Task> page, Wrapper wrapper);
    List<Map<String, Object>> getTaskStatistics();
}
