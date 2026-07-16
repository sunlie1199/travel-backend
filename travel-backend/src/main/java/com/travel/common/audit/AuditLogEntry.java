package com.travel.common.audit;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("audit_logs")
public class AuditLogEntry {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String module;

    private String action;

    private String resourceId;

    private String resourceName;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Object detail;

    private String ip;

    private Integer success;

    private String errorMsg;

    private LocalDateTime createdAt;
}
