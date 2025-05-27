package com.wms.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wms.common.Result;
import com.wms.entity.Notify;
import com.wms.service.NotifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notify")
public class NotifyController {

    @Autowired
    private NotifyService notifyService;

    @GetMapping("/list")
    public Result list(@RequestParam Long userId,
                       @RequestParam(defaultValue = "1") Integer pageNum,
                       @RequestParam(defaultValue = "3") Integer pageSize) {
        IPage<Notify> page = notifyService.listNotifyByPage(userId, pageNum, pageSize);
        return Result.suc(page);
    }

    @GetMapping("/unreadCount")
    public Result unreadCount(@RequestParam Long userId) {
        int count = notifyService.getUnreadCount(userId);
        return Result.suc(count);
    }

    @PostMapping("/read")
    public Result markAsRead(@RequestBody Map<String, Object> payload) {
        Long userId = Long.parseLong(payload.get("userId").toString());
        List<Long> notifyIds = (List<Long>) payload.get("notifyIds");
        boolean success = notifyService.markAsRead(userId, notifyIds);
        return success ? Result.suc() : Result.fail();
    }
}
