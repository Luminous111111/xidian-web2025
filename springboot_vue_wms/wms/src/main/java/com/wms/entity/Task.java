package com.wms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 任务表
 * </p>
 *
 * @author wms
 * @since 2025-05-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Task对象", description="任务表")
public class Task implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "任务标题")
    private String title;

    @ApiModelProperty(value = "任务描述")
    private String description;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "创建人（主管）")
    private Long creatorId;

    @TableField(exist = false)
    private String creatorName;

    @ApiModelProperty(value = "被分配人（员工）")
    private Long assigneeId;

    @TableField(exist = false)
    private String assigneeName;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "截止时间")
    private LocalDateTime deadline;


}
