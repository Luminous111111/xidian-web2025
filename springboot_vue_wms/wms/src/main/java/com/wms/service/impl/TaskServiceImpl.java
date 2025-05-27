package com.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wms.entity.Task;
import com.wms.mapper.TaskMapper;
import com.wms.service.TaskService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 * 任务表 服务实现类
 * </p>
 *
 * @author wms
 * @since 2025-05-08
 */
@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService {
    @Resource
    private TaskMapper taskMapper;

    @Override
    public IPage<Task> pageCC(IPage<Task> page, Wrapper<Task> wrapper) {
        return taskMapper.pageCC(page, wrapper);
    }
}
