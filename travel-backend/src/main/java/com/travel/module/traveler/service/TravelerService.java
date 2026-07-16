package com.travel.module.traveler.service;

import com.travel.module.traveler.dto.TravelerResponse;

public interface TravelerService {

    TravelerResponse getTravelerProfile(String travelerId);
}
