package com.wms.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.QueryPageParam;
import com.wms.common.Result;
import com.wms.entity.Task;
import com.wms.entity.TaskComment;
import com.wms.entity.TaskCommentRequest;
import com.wms.service.TaskCommentService;
import com.wms.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class TaskControllerTest {

  @InjectMocks
  private TaskController taskController;

  @Mock
  private TaskService taskService;

  @Mock
  private TaskCommentService taskCommentService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testSaveTask() {
    // 准备测试数据
    Task task = new Task();
    task.setTitle("测试任务");
    task.setDescription("这是一个测试任务");
    task.setStatus(0);
    task.setDeadline(LocalDateTime.now().plusDays(7));

    // 模拟service层行为
    when(taskService.save(any(Task.class))).thenReturn(true);

    // 执行测试
    Result result = taskController.save(task);

    // 验证结果
    assertEquals(200, result.getCode());
    verify(taskService, times(1)).save(any(Task.class));
  }

  @Test
  void testUpdateTask() {
    // 准备测试数据
    Task task = new Task();
    task.setId(1L);
    task.setTitle("更新后的任务");
    task.setStatus(1);

    // 模拟service层行为
    when(taskService.updateById(any(Task.class))).thenReturn(true);

    // 执行测试
    Result result = taskController.update(task);

    // 验证结果
    assertEquals(200, result.getCode());
    verify(taskService, times(1)).updateById(any(Task.class));
  }

  @Test
  void testDeleteTask() {
    // 模拟service层行为
    when(taskService.removeById(anyString())).thenReturn(true);

    // 执行测试
    Result result = taskController.del("1");

    // 验证结果
    assertEquals(200, result.getCode());
    verify(taskService, times(1)).removeById("1");
  }

  @Test
  void testGetTaskDetail() {
    // 准备测试数据
    Task task = new Task();
    task.setId(1L);
    task.setTitle("测试任务详情");

    // 模拟service层行为
    when(taskService.getById(anyLong())).thenReturn(task);

    // 执行测试
    Result result = taskController.getTaskDetail(1L);

    // 验证结果
    assertEquals(200, result.getCode());
    assertEquals(task, result.getData());
    verify(taskService, times(1)).getById(1L);
  }

  @Test
  void testListPage() {
    // 准备测试数据
    QueryPageParam query = new QueryPageParam();
    query.setPageNum(1);
    query.setPageSize(10);
    HashMap<String, Object> param = new HashMap<>();
    param.put("title", "测试");
    query.setParam(param);

    Page<Task> page = new Page<>();
    List<Task> records = new ArrayList<>();
    Task task = new Task();
    task.setTitle("测试任务");
    records.add(task);
    page.setRecords(records);
    page.setTotal(1);

    // 模拟service层行为
    when(taskService.pageCC(any(), any())).thenReturn(page);

    // 执行测试
    Result result = taskController.listPage(query);

    // 验证结果
    assertEquals(200, result.getCode());
    assertEquals(records, result.getData());
    assertEquals(1L, result.getTotal());
  }

  @Test
  void testAddComment() {
    // 准备测试数据
    Long taskId = 1L;
    TaskCommentRequest commentRequest = new TaskCommentRequest();
    commentRequest.setTaskId(taskId);
    commentRequest.setUserId(1);
    commentRequest.setContent("测试评论");

    Task task = new Task();
    task.setId(taskId);

    // 模拟service层行为
    when(taskService.getById(taskId)).thenReturn(task);
    when(taskCommentService.createComment(anyLong(), any(TaskCommentRequest.class)))
        .thenReturn(Result.suc());

    // 执行测试
    Result result = taskController.addComment(taskId, commentRequest);

    // 验证结果
    assertEquals(200, result.getCode());
    verify(taskService, times(1)).getById(taskId);
    verify(taskCommentService, times(1)).createComment(taskId, commentRequest);
  }
}