package com.wms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.entity.Notify;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface NotifyMapper extends BaseMapper<Notify> {
    int getUnreadCount(@Param("userId") Long userId);
}
