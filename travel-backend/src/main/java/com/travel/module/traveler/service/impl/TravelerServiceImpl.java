package com.travel.module.traveler.service.impl;

import com.travel.common.exception.BusinessException;
import com.travel.common.result.ResultCode;
import com.travel.module.destination.dto.DestinationResponse;
import com.travel.module.destination.entity.Destination;
import com.travel.module.destination.mapper.DestinationMapper;
import com.travel.module.traveler.dto.TravelerResponse;
import com.travel.module.traveler.service.TravelerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TravelerServiceImpl implements TravelerService {

    private static final Map<String, String> TRAVELER_NAMES = new HashMap<>();
    private static final Map<String, String> TRAVELER_AVATARS = new HashMap<>();

    static {
        TRAVELER_NAMES.put("male", "他的旅程");
        TRAVELER_NAMES.put("female", "她的旅程");
        TRAVELER_AVATARS.put("male", "");
        TRAVELER_AVATARS.put("female", "");
    }

    private final DestinationMapper destinationMapper;

    @Override
    public TravelerResponse getTravelerProfile(String travelerId) {
        if (!TRAVELER_NAMES.containsKey(travelerId)) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "旅行者不存在");
        }
        List<Destination> destinations = destinationMapper.selectByTravelerId(travelerId);
        List<DestinationResponse> destResponses = destinations.stream()
                .map(DestinationResponse::from)
                .collect(Collectors.toList());
        return TravelerResponse.of(travelerId, TRAVELER_NAMES.get(travelerId),
                TRAVELER_AVATARS.get(travelerId), destResponses);
    }
}
