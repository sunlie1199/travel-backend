package com.travel.module.shared.service.impl;

import com.travel.module.destination.mapper.TravelerDestinationMapper;
import com.travel.module.shared.dto.SharedResponse;
import com.travel.module.shared.service.SharedService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SharedServiceImpl implements SharedService {

    private final TravelerDestinationMapper travelerDestinationMapper;

    @Override
    public List<SharedResponse> getSharedFootprints() {
        List<Map<String, Object>> rows = travelerDestinationMapper.selectSharedFootprints();
        return rows.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private SharedResponse mapToResponse(Map<String, Object> row) {
        SharedResponse resp = new SharedResponse();
        resp.setName((String) row.get("name"));
        resp.setRegion((String) row.get("region"));

        Object maleVisited = row.get("maleVisited");
        resp.setMaleVisited(maleVisited != null && ((Number) maleVisited).intValue() == 1);

        Object femaleVisited = row.get("femaleVisited");
        resp.setFemaleVisited(femaleVisited != null && ((Number) femaleVisited).intValue() == 1);

        Object lat = row.get("latitude");
        Object lng = row.get("longitude");
        if (lat != null && lng != null) {
            Map<String, BigDecimal> coords = new HashMap<>(2);
            coords.put("lat", (BigDecimal) lat);
            coords.put("lng", (BigDecimal) lng);
            resp.setCoordinates(coords);
        }
        return resp;
    }
}
