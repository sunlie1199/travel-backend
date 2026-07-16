package com.travel.module.destination.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("destinations")
public class Destination {

    @TableId
    private String id;

    private String name;

    private String region;

    private String country;

    private String status;

    private String description;

    @TableField(value = "highlights", typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private Object highlights;

    private String bestSeason;

    @TableField(value = "images", typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private Object images;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private BigDecimal rating;

    @TableField(value = "tags", typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private Object tags;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
