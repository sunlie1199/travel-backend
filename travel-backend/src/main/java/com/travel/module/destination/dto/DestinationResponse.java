package com.travel.module.destination.dto;

import com.travel.module.destination.entity.Destination;
import lombok.Data;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class DestinationResponse {

    private String id;
    private String name;
    private String region;
    private String country;
    private String description;
    private Object highlights;
    private String bestSeason;
    private String status;
    private Object images;
    private Map<String, BigDecimal> coordinates;
    private BigDecimal rating;
    private Object tags;

    public static DestinationResponse from(Destination dest) {
        DestinationResponse resp = new DestinationResponse();
        resp.setId(dest.getId());
        resp.setName(dest.getName());
        resp.setRegion(dest.getRegion());
        resp.setCountry(dest.getCountry());
        resp.setStatus(dest.getStatus());
        resp.setDescription(dest.getDescription());
        resp.setHighlights(dest.getHighlights());
        resp.setBestSeason(dest.getBestSeason());
        resp.setImages(dest.getImages());
        resp.setRating(dest.getRating());
        resp.setTags(dest.getTags());

        if (dest.getLatitude() != null && dest.getLongitude() != null) {
            Map<String, BigDecimal> coords = new HashMap<>(2);
            coords.put("lat", dest.getLatitude());
            coords.put("lng", dest.getLongitude());
            resp.setCoordinates(coords);
        }
        return resp;
    }
}
