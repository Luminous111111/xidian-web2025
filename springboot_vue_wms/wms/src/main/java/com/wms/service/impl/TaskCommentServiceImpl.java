package com.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.Result;
import com.wms.entity.TaskComment;
import com.wms.entity.TaskCommentRequest;
import com.wms.entity.User;
import com.wms.mapper.TaskCommentMapper;
import com.wms.service.TaskCommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

/**
 * <p>
 * 任务评论表 服务实现类
 * </p>
 *
 * @author wms
 * @since 2025-05-28
 */
@Service
public class TaskCommentServiceImpl extends ServiceImpl<TaskCommentMapper, TaskComment> implements TaskCommentService {

    @Resource
    private TaskCommentMapper taskCommentMapper;

    public Result createComment(Long taskId, TaskCommentRequest commentRequest) {

        TaskComment taskComment = new TaskComment();
        //设置评论关联
        taskComment.setUserId(commentRequest.getUserId());
        taskComment.setTaskId(commentRequest.getTaskId());
        taskComment.setContent(commentRequest.getContent());
        taskComment.setCreateTime(LocalDateTime.now());
        // 保存评论
        boolean saveSuccess = taskCommentMapper.insert(taskComment) > 0;
        if (saveSuccess) {
            return Result.suc();
        } else return Result.fail();


    }

    @Override
    public Result getCommentsByTaskId(Long taskId, Integer pageNum, Integer pageSize) {
        // 创建分页对象
        Page<TaskComment> page = new Page<>(pageNum, pageSize);

        // 构建查询条件
        LambdaQueryWrapper<TaskComment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TaskComment::getTaskId, taskId)
                .orderByDesc(TaskComment::getCreateTime);

        // 执行分页查询
        IPage<TaskComment> commentPage = taskCommentMapper.selectPage(page, queryWrapper);

        // 返回结果
        return Result.suc(commentPage.getRecords(), commentPage.getTotal());
    }

}
