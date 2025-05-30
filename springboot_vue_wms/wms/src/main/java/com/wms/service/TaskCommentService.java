package com.wms.service;

import com.wms.common.Result;
import com.wms.entity.TaskComment;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wms.entity.TaskCommentRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 任务评论表 服务类
 * </p>
 *
 * @author wms
 * @since 2025-05-28
 */
public interface TaskCommentService extends IService<TaskComment> {
    public Result createComment(Long taskId, TaskCommentRequest commentRequest);
    Result getCommentsByTaskId(Long taskId, Integer pageNum, Integer pageSize);

}
