package com.contentflow.publish.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("publish_record")
public class PublishRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String taskId;
    private String platform;
    private String status;          // SUCCESS, FAILED
    private String externalId;      // 平台返回的发布ID
    private String errorMsg;
    private String responseData;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}