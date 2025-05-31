package com.wms.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.QueryPageParam;
import com.wms.common.Result;
import com.wms.entity.Task;
import com.wms.entity.TaskComment;
import com.wms.entity.TaskCommentRequest;
import com.wms.service.TaskCommentService;
import com.wms.service.TaskService;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 任务表 前端控制器
 * </p>
 *
 * @author wms
 * @since 2025-05-08
 */
@RestController
@RequestMapping("/task")
public class TaskController {
    //新增
    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskCommentService taskCommentService;
    @PostMapping("/save")
    public Result save(@RequestBody Task task){
        System.out.println("Received task for save: " + task);
        System.out.println("Deadline type: " + (task.getDeadline() != null ? task.getDeadline().getClass() : "null"));
        return taskService.save(task)?Result.suc():Result.fail();
    }

    @PostMapping("/update")
    public Result update(@RequestBody Task task){
        System.out.println("Received task for update: " + task);
        System.out.println("Deadline type: " + (task.getDeadline() != null ? task.getDeadline().getClass() : "null"));
        return taskService.updateById(task)?Result.suc():Result.fail();
    }
//    @PostMapping("/save")
//    public Result save(@RequestBody Task task){
//        return taskService.save(task)?Result.suc():Result.fail();
//    }
//    //更新
//    @PostMapping("/update")
//    public Result update(@RequestBody Task task){
//        return taskService.updateById(task)?Result.suc():Result.fail();
//    }
    //删除
    @GetMapping("/del")
    public Result del(@RequestParam String id){
        return taskService.removeById(id)?Result.suc():Result.fail();
    }

//    @GetMapping("/dell")
//    public Result dell(){
//        System.out.println('1');
//        return Result.suc();
//    }
    @PostMapping("/listPage")
    public Result listPage(@RequestBody QueryPageParam query){
        HashMap param = query.getParam();
        //String status = (String)param.get("status");
        String title = (String)param.get("title");
        String assigneeName = (String)param.get("assigneeName");
        Object statusObj = param.get("status");
        String status = statusObj != null ? statusObj.toString() : null;
//String status = (String)param.get("status");

        Page<Task> page = new Page();
        page.setCurrent(query.getPageNum());
        page.setSize(query.getPageSize());

        LambdaQueryWrapper<Task> lambdaQueryWrapper = new LambdaQueryWrapper();
        if(StringUtils.isNotBlank(title) && !"null".equals(title)){
            lambdaQueryWrapper.like(Task::getTitle,title);
            System.out.println('2');
        }
        if(StringUtils.isNotBlank(assigneeName) && !"null".equals(assigneeName)){
            lambdaQueryWrapper.eq(Task::getAssigneeName,assigneeName);
            System.out.println('2');
        }
        if(StringUtils.isNotBlank(status)){
            lambdaQueryWrapper.eq(Task::getStatus,status);
        }
//        if(StringUtils.isNotBlank(status)){
//            lambdaQueryWrapper.eq(Task::getStatus,status);
//        }
//        if(StringUtils.isNotBlank(title)){
//            lambdaQueryWrapper.eq(Task::getTitle,title);
//        }

        //IPage result = TaskService.pageC(page);
        IPage result = taskService.pageCC(page,lambdaQueryWrapper);

        System.out.println("total=="+result.getTotal());

        return Result.suc(result.getRecords(),result.getTotal());
    }


    @PostMapping("/listPageCheck")
    public Result listPageCheck(@RequestBody QueryPageParam query){
        HashMap param = query.getParam();
        //String status = (String)param.get("status");
        String title = (String)param.get("title");
        String assigneeName = (String)param.get("assigneeName");
        Object statusObj = param.get("status");
        String status = statusObj != null ? statusObj.toString() : null;

        Page<Task> page = new Page();
        page.setCurrent(query.getPageNum());
        page.setSize(query.getPageSize());

        LambdaQueryWrapper<Task> lambdaQueryWrapper = new LambdaQueryWrapper();
        if(StringUtils.isNotBlank(title) && !"null".equals(title)){
            lambdaQueryWrapper.like(Task::getTitle,title);
            System.out.println('2');
        }
        if(StringUtils.isNotBlank(assigneeName) && !"null".equals(assigneeName)){
            lambdaQueryWrapper.eq(Task::getAssigneeName,assigneeName);
            System.out.println('2');
        }
        if(StringUtils.isNotBlank(status)){
            lambdaQueryWrapper.eq(Task::getStatus,status);
        }
//        if(StringUtils.isNotBlank(status)){
//            lambdaQueryWrapper.eq(Task::getStatus,status);
//        }
//        if(StringUtils.isNotBlank(title)){
//            lambdaQueryWrapper.eq(Task::getTitle,title);
//        }

        //IPage result = TaskService.pageC(page);
        IPage result = taskService.pageCC(page,lambdaQueryWrapper);

        System.out.println("total=="+result.getTotal());

        return Result.suc(result.getRecords(),result.getTotal());
    }


    @PostMapping("/listPageUpdate")
    public Result listPageUpdate(@RequestBody QueryPageParam query){
        HashMap param = query.getParam();
        //String status = (String)param.get("status");
        String title = (String)param.get("title");
        String assigneeName = (String)param.get("assigneeName");
        Object statusObj = param.get("status");
        String status = statusObj != null ? statusObj.toString() : null;

        // 获取当前登录用户名（根据你的认证方式调整）
        //String currentUsername = (String) request.getAttribute("username");

        Page<Task> page = new Page();
        page.setCurrent(query.getPageNum());
        page.setSize(query.getPageSize());

        LambdaQueryWrapper<Task> lambdaQueryWrapper = new LambdaQueryWrapper();
        if(StringUtils.isNotBlank(title) && !"null".equals(title)){
            lambdaQueryWrapper.like(Task::getTitle,title);
            System.out.println('2');
        }
        if(StringUtils.isNotBlank(assigneeName) && !"null".equals(assigneeName)){
            lambdaQueryWrapper.eq(Task::getAssigneeName,assigneeName);
            System.out.println('2');
        }
        if(StringUtils.isNotBlank(status)){
            lambdaQueryWrapper.eq(Task::getStatus,status);
        }
//        if(StringUtils.isNotBlank(status)){
//            lambdaQueryWrapper.eq(Task::getStatus,status);
//        }
//        if(StringUtils.isNotBlank(title)){
//            lambdaQueryWrapper.eq(Task::getTitle,title);
//        }

        //IPage result = TaskService.pageC(page);
        IPage result = taskService.pageCC(page,lambdaQueryWrapper);

        System.out.println("total=="+result.getTotal());

        return Result.suc(result.getRecords(),result.getTotal());
    }

//    @GetMapping("/statistics")
//    public Result getTaskStatistics() {
//        List<Map<String, Object>> statistics = taskService.getTaskStatistics();
//        return Result.suc(statistics);
//    }

    @GetMapping("/detail")
    public Result getTaskDetail(@RequestParam Long id) {
        Task task = taskService.getById(id);
        if (task != null) {
            return Result.suc(task);
        } else {
            return Result.fail();
        }
    }

    @PostMapping("/{taskId}/comments")
    public Result addComment(@PathVariable Long taskId,
                             @RequestBody TaskCommentRequest commentRequest) {
        try {
            Task task = taskService.getById(taskId);
            if (task == null) {
                return Result.fail("没有相关任务");
            }
            return taskCommentService.createComment(taskId, commentRequest);
        } catch (Exception e) {
            return Result.fail("添加评论失败，请稍后再试");
        }
    }

    //新增加的
    @GetMapping("/{taskId}/listcomments")
    public Result listComments(@PathVariable Long taskId,
                               @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                               @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        try {
            Task task = taskService.getById(taskId);
            if (task == null) {
                return Result.fail("没有相关任务");
            }

            return taskCommentService.getCommentsByTaskId(taskId, pageNum, pageSize);
        } catch (Exception e) {
            return Result.fail("获取评论列表失败");
        }
    }

}
