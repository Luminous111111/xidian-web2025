package com.wms.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.QueryPageParam;
import com.wms.common.Result;
import com.wms.entity.Menu;
import com.wms.entity.User;
import com.wms.service.MenuService;
import com.wms.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserControllerTest {

  @InjectMocks
  private UserController userController;

  @Mock
  private UserService userService;

  @Mock
  private MenuService menuService;

  private User testUser;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    // 初始化测试用户数据
    testUser = new User();
    testUser.setId(1);
    testUser.setNo("test001");
    testUser.setName("测试用户");
    testUser.setPassword("123456");
    testUser.setAge(25);
    testUser.setSex(1);
    testUser.setPhone("13800138000");
    testUser.setRoleId(2);
    testUser.setIsvalid("Y");
  }

  @Test
  void testSaveUser() {
    // 模拟service层行为
    when(userService.save(any(User.class))).thenReturn(true);

    // 执行测试
    Result result = userController.save(testUser);

    // 验证结果
    assertEquals(200, result.getCode());
    verify(userService, times(1)).save(any(User.class));
  }

  @Test
  void testUpdateUser() {
    // 模拟service层行为
    when(userService.updateById(any(User.class))).thenReturn(true);

    // 执行测试
    Result result = userController.update(testUser);

    // 验证结果
    assertEquals(200, result.getCode());
    verify(userService, times(1)).updateById(any(User.class));
  }

  @Test
  void testDeleteUser() {
    // 模拟service层行为
    when(userService.removeById(anyString())).thenReturn(true);

    // 执行测试
    Result result = userController.del("1");

    // 验证结果
    assertEquals(200, result.getCode());
    verify(userService, times(1)).removeById("1");
  }

  @Test
  void testListPage() {
    // 准备测试数据
    QueryPageParam query = new QueryPageParam();
    query.setPageNum(1);
    query.setPageSize(10);
    HashMap<String, Object> param = new HashMap<>();
    param.put("name", "测试");
    query.setParam(param);

    Page<User> page = new Page<>();
    List<User> records = new ArrayList<>();
    records.add(testUser);
    page.setRecords(records);
    page.setTotal(1);

    // 模拟service层行为
    when(userService.pageCC(any(), any())).thenReturn(page);

    // 执行测试
    Result result = userController.listPageC1(query);

    // 验证结果
    assertEquals(200, result.getCode());
    assertEquals(records, result.getData());
    assertEquals(1L, result.getTotal());
  }

  @Test
  void testGetUserName() {
    // 模拟service层行为
    when(userService.getById(anyInt())).thenReturn(testUser);

    // 执行测试
    Result result = userController.getUserName(1);

    // 验证结果
    assertEquals(200, result.getCode());
    assertEquals("测试用户", result.getData());
  }

  @Test
  void testGetUserNameNotFound() {
    // 模拟service层行为
    when(userService.getById(anyInt())).thenReturn(null);

    // 执行测试
    Result result = userController.getUserName(999);

    // 验证结果
    assertEquals(400, result.getCode());
    assertEquals("用户不存在", result.getMsg());
  }
}