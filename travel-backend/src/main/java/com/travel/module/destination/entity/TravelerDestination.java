package com.travel.module.destination.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("traveler_destinations")
public class TravelerDestination {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String destinationId;

    private String travelerId;

    private LocalDateTime createdAt;
}
