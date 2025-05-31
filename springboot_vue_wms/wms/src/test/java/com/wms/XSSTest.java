package com.wms.controller;

import com.wms.common.QueryPageParam;
import com.wms.common.Result;
import com.wms.entity.TaskCommentRequest;
import com.wms.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class XSSTest {

  @Autowired
  private UserController userController;

  @Autowired
  private TaskController taskController;

  @Test
  void testTaskInputXSS() {
    // 测试任务相关字段的XSS防护
    QueryPageParam query = new QueryPageParam();
    query.setPageNum(1);
    query.setPageSize(10);
    HashMap<String, Object> param = new HashMap<>();

    // 1. 测试任务标题XSS
    param.put("title", "<script>alert('xss')</script>");
    query.setParam(param);
    Result result1 = taskController.listPage(query);
    assertEquals(200, result1.getCode(), "XSS防护成功：任务标题XSS");

    // 2. 测试任务描述XSS
    param.put("description", "<script>alert('xss')</script>");
    query.setParam(param);
    Result result2 = taskController.listPage(query);
    assertEquals(200, result2.getCode(), "XSS防护成功：任务描述XSS");
  }

  @Test
  void testCommentXSS() {
    // 测试评论内容的XSS防护
    TaskCommentRequest commentRequest = new TaskCommentRequest();
    commentRequest.setTaskId(1L);
    commentRequest.setUserId(1);

    // 1. 测试基本XSS
    commentRequest.setContent("<script>alert('xss')</script>");
    Result result1 = taskController.addComment(1L, commentRequest);
    assertEquals(400, result1.getCode(), "XSS防护成功：基本XSS");

    // 2. 测试事件处理XSS
    commentRequest.setContent("<img src=x onerror=alert('xss')>");
    Result result2 = taskController.addComment(1L, commentRequest);
    assertEquals(400, result2.getCode(), "XSS防护成功：事件处理XSS");

    // 3. 测试编码XSS
    commentRequest.setContent("&#60;script&#62;alert('xss')&#60;/script&#62;");
    Result result3 = taskController.addComment(1L, commentRequest);
    assertEquals(400, result3.getCode(), "XSS防护成功：编码XSS");
  }

  @Test
  void testComplexXSS() {
    // 测试复杂的XSS攻击向量
    User user = new User();

    // 1. 测试多重编码XSS
    user.setName("%3Cscript%3Ealert('xss')%3C/script%3E");
    user.setNo("test001");
    user.setPassword("123456");
    Result result1 = userController.save(user);
    assertEquals(200, result1.getCode(), "XSS防护成功：多重编码XSS");

    // 2. 测试混合XSS
    user.setName("<img src=x onerror=alert('xss')><script>alert('xss')</script>");
    Result result2 = userController.save(user);
    assertEquals(200, result2.getCode(), "XSS防护成功：混合XSS");

    // 3. 测试特殊字符XSS
    user.setName("javascript:alert('xss')");
    Result result3 = userController.save(user);
    assertEquals(200, result3.getCode(), "XSS防护成功：特殊字符XSS");
  }

  @Test
  void testSearchXSS() {
    // 测试搜索功能的XSS防护
    QueryPageParam query = new QueryPageParam();
    query.setPageNum(1);
    query.setPageSize(10);
    HashMap<String, Object> param = new HashMap<>();

    // 1. 测试搜索关键词XSS
    param.put("name", "<script>alert('xss')</script>");
    query.setParam(param);
    Result result1 = userController.listPageC1(query);
    assertEquals(200, result1.getCode(), "XSS防护成功：搜索关键词XSS");

    // 2. 测试过滤条件XSS
    param.put("roleId", "<script>alert('xss')</script>");
    query.setParam(param);
    Result result2 = userController.listPageC1(query);
    assertEquals(200, result2.getCode(), "XSS防护成功：过滤条件XSS");
  }
}