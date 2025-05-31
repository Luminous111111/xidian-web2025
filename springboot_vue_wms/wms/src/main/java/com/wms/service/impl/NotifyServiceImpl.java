package com.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wms.entity.Notify;
import com.wms.mapper.NotifyMapper;
import com.wms.service.NotifyService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotifyServiceImpl extends ServiceImpl<NotifyMapper, Notify> implements NotifyService {

    @Override
    public IPage<Notify> listNotifyByPage(Long userId, int pageNum, int pageSize) {
        Page<Notify> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Notify> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).orderByDesc("create_time");
        return this.page(page, queryWrapper);
    }

    @Override
    public int getUnreadCount(Long userId) {
        return this.baseMapper.getUnreadCount(userId);
    }

    @Override
    public boolean markAsRead(Long userId, List<Long> notifyIds) {
        UpdateWrapper<Notify> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("user_id", userId)
                     .in("id", notifyIds)
                     .set("status", 1); // 1 表示已读
        return this.update(updateWrapper);
    }
}
