package com.travel.module.shared.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Data
public class SharedResponse {

    private String name;
    private Boolean maleVisited;
    private Boolean femaleVisited;
    private String region;
    private Map<String, BigDecimal> coordinates;
}
