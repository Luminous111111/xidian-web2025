package com.wms.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.QueryPageParam;
import com.wms.common.Result;
import com.wms.entity.Task;
import com.wms.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
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
    @PostMapping("/save")
    public Result save(@RequestBody Task task){
        return taskService.save(task)?Result.suc():Result.fail("任务不存在");
    }
    //更新
    @PostMapping("/update")
    public Result update(@RequestBody Task task){
        return taskService.updateById(task)?Result.suc():Result.fail("任务不存在");
    }
    //删除
    @GetMapping("/del")
    public Result del(@RequestParam String id){
        return taskService.removeById(id)?Result.suc():Result.fail("任务不存在");
    }

//    @GetMapping("/dell")
//    public Result dell(){
//        System.out.println('1');
//        return Result.suc();
//    }
    @PostMapping("/listPage")
    public Result listPage(@RequestBody QueryPageParam query){
        HashMap<String, Object> param = query.getParam();

        String title = (String) param.get("title");
        Integer status = null;
        if (param.get("status") instanceof Integer) {
            status = (Integer) param.get("status");
        } else if (param.get("status") instanceof String) {
            try {
                status = Integer.parseInt((String) param.get("status"));
            } catch (Exception ignored) {}
        }

        Long assigneeId = null;
        if (param.get("assignee_id") != null) {
            try {
                assigneeId = Long.parseLong(param.get("assignee_id").toString());
            } catch (NumberFormatException ignored) {}
        }



        Page<Task> page = new Page<>();
        page.setCurrent(query.getPageNum());
        page.setSize(query.getPageSize());

        LambdaQueryWrapper<Task> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(title)) {
            lambdaQueryWrapper.like(Task::getTitle, title);
        }

        if (status != null && status >= 0) {
            lambdaQueryWrapper.eq(Task::getStatus, status);
        }

        if (assigneeId != null) {
            lambdaQueryWrapper.eq(Task::getAssigneeId, assigneeId);
        }

        try {
            IPage<Task> result = taskService.pageCC(page, lambdaQueryWrapper);
            return Result.suc(result.getRecords(), result.getTotal());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("查询异常");
        }
    }
    @GetMapping("/statistics")
    public Result getTaskStatistics() {
        List<Map<String, Object>> statistics = taskService.getTaskStatistics();
        return Result.suc(statistics);
    }

}
