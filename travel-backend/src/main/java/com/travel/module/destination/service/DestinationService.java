package com.travel.module.destination.service;

import com.travel.module.destination.dto.DestinationRequest;
import com.travel.module.destination.dto.DestinationResponse;

import java.util.List;

public interface DestinationService {

    List<DestinationResponse> listAll(String region);

    DestinationResponse getById(String id);

    DestinationResponse create(DestinationRequest request);

    DestinationResponse update(String id, DestinationRequest request);

    void delete(String id);
}
