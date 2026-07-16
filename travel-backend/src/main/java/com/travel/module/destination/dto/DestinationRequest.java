package com.travel.module.destination.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Data
public class DestinationRequest {

    @NotBlank(message = "目的地名称不能为空")
    private String name;

    @NotBlank(message = "地区不能为空")
    private String region;

    @NotBlank(message = "国家不能为空")
    private String country;

    private String status;

    @NotBlank(message = "描述不能为空")
    private String description;

    private List<String> highlights;

    private String bestSeason;

    private List<ImageItem> images;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private BigDecimal rating;

    private List<String> tags;

    @NotNull(message = "归属者不能为空")
    private String owner;

    @Data
    public static class ImageItem {
        private String id;
        private String url;
        private String alt;
        private Integer width;
        private Integer height;
    }
}
