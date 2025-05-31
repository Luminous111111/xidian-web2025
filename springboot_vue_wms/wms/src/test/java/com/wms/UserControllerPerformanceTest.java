package com.wms.controller;

import com.wms.common.QueryPageParam;
import com.wms.common.Result;
import com.wms.entity.User;
import com.wms.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class UserControllerPerformanceTest {

  @Autowired
  private UserController userController;

  @Autowired
  private UserService userService;

  private User testUser;
  private static final int THREAD_COUNT = 100; // 并发线程数
  private static final int REQUEST_COUNT = 1000; // 总请求数

  @BeforeEach
  void setUp() {
    // 初始化测试用户数据
    testUser = new User();
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
  void testConcurrentLogin() throws InterruptedException {
    // 准备测试数据
    List<User> users = new ArrayList<>();
    for (int i = 0; i < REQUEST_COUNT; i++) {
      User user = new User();
      user.setNo("test" + i);
      user.setPassword("123456");
      users.add(user);
    }

    // 创建线程池
    ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
    CountDownLatch latch = new CountDownLatch(REQUEST_COUNT);
    long startTime = System.currentTimeMillis();

    // 并发执行登录请求
    for (User user : users) {
      executorService.execute(() -> {
        try {
          Result result = userController.login(user);
          assertNotNull(result);
        } finally {
          latch.countDown();
        }
      });
    }

    // 等待所有请求完成
    latch.await();
    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;

    // 输出性能指标
    System.out.println("并发登录测试结果：");
    System.out.println("总请求数: " + REQUEST_COUNT);
    System.out.println("并发线程数: " + THREAD_COUNT);
    System.out.println("总耗时: " + duration + "ms");
    System.out.println("平均响应时间: " + (duration / REQUEST_COUNT) + "ms");
    System.out.println("TPS: " + (REQUEST_COUNT * 1000.0 / duration));

    executorService.shutdown();
  }

  @Test
  void testConcurrentListPage() throws InterruptedException {
    // 准备测试数据
    List<QueryPageParam> queries = new ArrayList<>();
    for (int i = 0; i < REQUEST_COUNT; i++) {
      QueryPageParam query = new QueryPageParam();
      query.setPageNum(1);
      query.setPageSize(10);
      HashMap<String, Object> param = new HashMap<>();
      param.put("name", "测试" + i);
      query.setParam(param);
      queries.add(query);
    }

    // 创建线程池
    ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
    CountDownLatch latch = new CountDownLatch(REQUEST_COUNT);
    long startTime = System.currentTimeMillis();

    // 并发执行分页查询
    for (QueryPageParam query : queries) {
      executorService.execute(() -> {
        try {
          Result result = userController.listPageC1(query);
          assertNotNull(result);
        } finally {
          latch.countDown();
        }
      });
    }

    // 等待所有请求完成
    latch.await();
    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;

    // 输出性能指标
    System.out.println("并发分页查询测试结果：");
    System.out.println("总请求数: " + REQUEST_COUNT);
    System.out.println("并发线程数: " + THREAD_COUNT);
    System.out.println("总耗时: " + duration + "ms");
    System.out.println("平均响应时间: " + (duration / REQUEST_COUNT) + "ms");
    System.out.println("TPS: " + (REQUEST_COUNT * 1000.0 / duration));

    executorService.shutdown();
  }

  @Test
  void testEndurance() throws InterruptedException {
    // 准备测试数据
    int durationMinutes = 5; // 测试持续时间（分钟）
    long endTime = System.currentTimeMillis() + (durationMinutes * 60 * 1000);
    int successCount = 0;
    int failureCount = 0;
    long totalResponseTime = 0;

    // 执行耐久测试
    while (System.currentTimeMillis() < endTime) {
      long startTime = System.currentTimeMillis();
      try {
        Result result = userController.listPageC1(new QueryPageParam());
        if (result.getCode() == 200) {
          successCount++;
        } else {
          failureCount++;
        }
      } catch (Exception e) {
        failureCount++;
      }
      totalResponseTime += (System.currentTimeMillis() - startTime);
      Thread.sleep(100); // 控制请求频率
    }

    // 输出性能指标
    System.out.println("耐久测试结果：");
    System.out.println("测试持续时间: " + durationMinutes + "分钟");
    System.out.println("成功请求数: " + successCount);
    System.out.println("失败请求数: " + failureCount);
    System.out.println("成功率: " + (successCount * 100.0 / (successCount + failureCount)) + "%");
    System.out.println("平均响应时间: " + (totalResponseTime / (successCount + failureCount)) + "ms");
  }

  @Test
  void testStress() throws InterruptedException {
    // 准备测试数据
    int maxThreads = 500; // 最大线程数
    ExecutorService executorService = Executors.newFixedThreadPool(maxThreads);
    CountDownLatch latch = new CountDownLatch(maxThreads);
    long startTime = System.currentTimeMillis();

    // 执行压力测试
    for (int i = 0; i < maxThreads; i++) {
      executorService.execute(() -> {
        try {
          for (int j = 0; j < 10; j++) { // 每个线程执行10次请求
            userController.listPageC1(new QueryPageParam());
            Thread.sleep(100); // 控制请求频率
          }
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
          latch.countDown();
        }
      });
    }

    // 等待所有请求完成
    latch.await();
    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;

    // 输出性能指标
    System.out.println("压力测试结果：");
    System.out.println("最大并发线程数: " + maxThreads);
    System.out.println("总请求数: " + (maxThreads * 10));
    System.out.println("总耗时: " + duration + "ms");
    System.out.println("平均响应时间: " + (duration / (maxThreads * 10)) + "ms");
    System.out.println("TPS: " + (maxThreads * 10 * 1000.0 / duration));

    executorService.shutdown();
  }
}