package com.travel.module.stats.service.impl;

import com.travel.module.destination.mapper.DestinationMapper;
import com.travel.module.destination.mapper.TravelerDestinationMapper;
import com.travel.module.stats.dto.StatsResponse;
import com.travel.module.stats.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final TravelerDestinationMapper travelerDestinationMapper;
    private final DestinationMapper destinationMapper;

    @Override
    public StatsResponse getStats() {
        int maleCount = travelerDestinationMapper.countByTravelerId("male");
        int femaleCount = travelerDestinationMapper.countByTravelerId("female");
        int sharedCount = destinationMapper.countShared();
        int totalRegions = destinationMapper.countDistinctRegions();
        int visitedCount = destinationMapper.countByStatus("visited");
        int wishlistCount = destinationMapper.countByStatus("wishlist");
        java.util.List<String> countries = destinationMapper.selectDistinctCountries();

        return StatsResponse.builder()
                .maleCount(maleCount)
                .femaleCount(femaleCount)
                .sharedCount(sharedCount)
                .totalRegions(totalRegions)
                .countries(countries)
                .visitedCount(visitedCount)
                .wishlistCount(wishlistCount)
                .build();
    }
}
