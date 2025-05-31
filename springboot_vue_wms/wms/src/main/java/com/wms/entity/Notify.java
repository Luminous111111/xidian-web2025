package com.wms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("notify")
public class Notify {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createTime;
    private Integer status;
    private Long userId;
    private Long taskId;
}
