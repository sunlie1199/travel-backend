package com.travel.module.stats.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StatsResponse {

    private Integer maleCount;
    private Integer femaleCount;
    private Integer sharedCount;
    private Integer totalRegions;
    private List<String> countries;
    private Integer visitedCount;
    private Integer wishlistCount;
}
