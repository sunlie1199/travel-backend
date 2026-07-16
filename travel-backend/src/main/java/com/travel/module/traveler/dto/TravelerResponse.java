package com.travel.module.traveler.dto;

import com.travel.module.destination.dto.DestinationResponse;
import lombok.Data;

import java.util.List;

@Data
public class TravelerResponse {

    private String id;
    private String name;
    private String avatar;
    private List<DestinationResponse> destinations;

    public static TravelerResponse of(String travelerId, String name, String avatar,
                                       List<DestinationResponse> destinations) {
        TravelerResponse resp = new TravelerResponse();
        resp.setId(travelerId);
        resp.setName(name);
        resp.setAvatar(avatar);
        resp.setDestinations(destinations);
        return resp;
    }
}
