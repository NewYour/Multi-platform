package com.contentflow.publish.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("publish_task")
public class PublishTask {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String taskId;          // 业务唯一ID
    private Long userId;
    private Long contentId;
    private String platforms;       // 存储JSON字符串
    private Integer simulate;       // 0=真实发布, 1=模拟
    private String status;          // PENDING, PROCESSING, SUCCESS, PARTIAL_SUCCESS, FAILED
    private String result;          // JSON结果
    private Integer retryCount;
    private Integer maxRetries;
    private String errorMessage;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}