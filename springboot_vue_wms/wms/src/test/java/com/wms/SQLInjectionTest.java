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
public class SQLInjectionTest {

  @Autowired
  private UserController userController;

  @Autowired
  private TaskController taskController;



  @Test
  void testTaskSearchInjection() {
    // 测试任务搜索接口的SQL注入
    QueryPageParam query = new QueryPageParam();
    query.setPageNum(1);
    query.setPageSize(10);
    HashMap<String, Object> param = new HashMap<>();

    // 1. 测试标题注入
    param.put("title", "' OR '1'='1");
    query.setParam(param);
    Result result1 = taskController.listPage(query);
    assertEquals(200, result1.getCode(), "SQL注入防护成功：标题注入");

    // 2. 测试描述注入
    param.put("description", "' OR '1'='1");
    query.setParam(param);
    Result result2 = taskController.listPage(query);
    assertEquals(200, result2.getCode(), "SQL注入防护成功：描述注入");

    // 3. 测试状态注入
    param.put("status", "' OR '1'='1");
    query.setParam(param);
    Result result3 = taskController.listPage(query);
    assertEquals(200, result3.getCode(), "SQL注入防护成功：状态注入");
  }

  @Test
  void testUserSearchInjection() {
    // 测试用户搜索接口的SQL注入
    QueryPageParam query = new QueryPageParam();
    query.setPageNum(1);
    query.setPageSize(10);
    HashMap<String, Object> param = new HashMap<>();

    // 1. 测试用户名注入
    param.put("name", "' OR '1'='1");
    query.setParam(param);
    Result result1 = userController.listPageC1(query);
    assertEquals(200, result1.getCode(), "SQL注入防护成功：用户名注入");

    // 2. 测试工号注入
    param.put("no", "' OR '1'='1");
    query.setParam(param);
    Result result2 = userController.listPageC1(query);
    assertEquals(200, result2.getCode(), "SQL注入防护成功：工号注入");

    // 3. 测试手机号注入
    param.put("phone", "' OR '1'='1");
    query.setParam(param);
    Result result3 = userController.listPageC1(query);
    assertEquals(200, result3.getCode(), "SQL注入防护成功：手机号注入");
  }

  @Test
  void testComplexInjection() {
    // 测试复杂的SQL注入场景
    QueryPageParam query = new QueryPageParam();
    query.setPageNum(1);
    query.setPageSize(10);
    HashMap<String, Object> param = new HashMap<>();

    // 1. 测试多条件注入
    param.put("name", "' OR '1'='1");
    param.put("no", "' OR '1'='1");
    param.put("phone", "' OR '1'='1");
    query.setParam(param);
    Result result1 = userController.listPageC1(query);
    assertEquals(200, result1.getCode(), "SQL注入防护成功：多条件注入");

    // 2. 测试特殊字符注入
    param.put("name", "'; DROP TABLE user; --");
    query.setParam(param);
    Result result2 = userController.listPageC1(query);
    assertEquals(200, result2.getCode(), "SQL注入防护成功：特殊字符注入");

    // 3. 测试编码注入
    param.put("name", "%27%20OR%201%3D1");
    query.setParam(param);
    Result result3 = userController.listPageC1(query);
    assertEquals(200, result3.getCode(), "SQL注入防护成功：编码注入");
  }

  @Test
  void testBlindInjection() {
    // 测试盲注攻击
    QueryPageParam query = new QueryPageParam();
    query.setPageNum(1);
    query.setPageSize(10);
    HashMap<String, Object> param = new HashMap<>();

    // 1. 测试布尔盲注
    param.put("name", "' AND 1=1--");
    query.setParam(param);
    Result result1 = userController.listPageC1(query);
    assertEquals(200, result1.getCode(), "SQL注入防护成功：布尔盲注");

    // 2. 测试时间盲注
    param.put("name", "' AND SLEEP(5)--");
    query.setParam(param);
    Result result2 = userController.listPageC1(query);
    assertEquals(200, result2.getCode(), "SQL注入防护成功：时间盲注");

    // 3. 测试报错盲注
    param.put("name",
        "' AND (SELECT 1 FROM (SELECT COUNT(*),CONCAT(VERSION(),FLOOR(RAND(0)*2))x FROM INFORMATION_SCHEMA.TABLES GROUP BY x)a)--");
    query.setParam(param);
    Result result3 = userController.listPageC1(query);
    assertEquals(200, result3.getCode(), "SQL注入防护成功：报错盲注");
  }
}