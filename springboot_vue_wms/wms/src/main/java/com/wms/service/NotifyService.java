package com.wms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wms.entity.Notify;

import java.util.List;

public interface NotifyService extends IService<Notify> {
    IPage<Notify> listNotifyByPage(Long userId, int pageNum, int pageSize);
    int getUnreadCount(Long userId);
    boolean markAsRead(Long userId, List<Long> notifyIds);
}
