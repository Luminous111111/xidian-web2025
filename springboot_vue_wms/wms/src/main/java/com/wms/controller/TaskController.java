package com.wms.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.QueryPageParam;
import com.wms.common.Result;
import com.wms.entity.Task;
import com.wms.service.TaskService;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

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
    @PostMapping("/save")
    public Result save(@RequestBody Task task){
        return taskService.save(task)?Result.suc():Result.fail();
    }
    //更新
    @PostMapping("/update")
    public Result update(@RequestBody Task task){
        return taskService.updateById(task)?Result.suc():Result.fail();
    }
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

        Page<Task> page = new Page();
        page.setCurrent(query.getPageNum());
        page.setSize(query.getPageSize());

        LambdaQueryWrapper<Task> lambdaQueryWrapper = new LambdaQueryWrapper();
        if(StringUtils.isNotBlank(title) && !"null".equals(title)){
            lambdaQueryWrapper.like(Task::getTitle,title);
            System.out.println('2');
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

    @GetMapping("/detail")
    public Result getTaskDetail(@RequestParam Long id) {
        Task task = taskService.getById(id);
        if (task != null) {
            return Result.suc(task);
        } else {
            return Result.fail("任务不存在");
        }
    }

}
