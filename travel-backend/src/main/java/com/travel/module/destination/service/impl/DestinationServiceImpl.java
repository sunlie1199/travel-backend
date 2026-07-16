package com.travel.module.destination.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.travel.common.audit.AuditLog;
import com.travel.common.exception.BusinessException;
import com.travel.common.result.ResultCode;
import com.travel.module.destination.dto.DestinationRequest;
import com.travel.module.destination.dto.DestinationResponse;
import com.travel.module.destination.entity.Destination;
import com.travel.module.destination.entity.TravelerDestination;
import com.travel.module.destination.mapper.DestinationMapper;
import com.travel.module.destination.mapper.TravelerDestinationMapper;
import com.travel.module.destination.service.DestinationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DestinationServiceImpl implements DestinationService {

    private final DestinationMapper destinationMapper;
    private final TravelerDestinationMapper travelerDestinationMapper;

    @Override
    public List<DestinationResponse> listAll(String region) {
        LambdaQueryWrapper<Destination> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(region)) {
            wrapper.eq(Destination::getRegion, region);
        }
        wrapper.orderByDesc(Destination::getCreatedAt);
        List<Destination> destinations = destinationMapper.selectList(wrapper);
        return destinations.stream().map(DestinationResponse::from).collect(Collectors.toList());
    }

    @Override
    public DestinationResponse getById(String id) {
        Destination dest = destinationMapper.selectById(id);
        if (dest == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "目的地不存在");
        }
        return DestinationResponse.from(dest);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @AuditLog(module = "DESTINATION", action = "CREATE",
              resourceId = "#result.id", resourceName = "#request.name")
    public DestinationResponse create(DestinationRequest request) {
        Destination dest = new Destination();
        dest.setId(generateDestinationId(request.getName(), request.getOwner()));
        dest.setName(request.getName());
        dest.setRegion(request.getRegion());
        dest.setCountry(request.getCountry());
        dest.setStatus(request.getStatus() != null ? request.getStatus() : "wishlist");
        dest.setDescription(request.getDescription());
        dest.setHighlights(request.getHighlights());
        dest.setBestSeason(request.getBestSeason());
        dest.setImages(request.getImages());
        dest.setLatitude(request.getLatitude());
        dest.setLongitude(request.getLongitude());
        dest.setRating(request.getRating() != null ? request.getRating() : new java.math.BigDecimal("4.5"));
        dest.setTags(request.getTags());

        destinationMapper.insert(dest);
        bindTravelers(dest.getId(), request.getOwner());

        return DestinationResponse.from(dest);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @AuditLog(module = "DESTINATION", action = "UPDATE",
              resourceId = "#id", resourceName = "#request.name")
    public DestinationResponse update(String id, DestinationRequest request) {
        Destination dest = destinationMapper.selectById(id);
        if (dest == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "目的地不存在");
        }

        dest.setName(request.getName());
        dest.setRegion(request.getRegion());
        dest.setCountry(request.getCountry());
        dest.setStatus(request.getStatus());
        dest.setDescription(request.getDescription());
        dest.setHighlights(request.getHighlights());
        dest.setBestSeason(request.getBestSeason());
        dest.setImages(request.getImages());
        dest.setLatitude(request.getLatitude());
        dest.setLongitude(request.getLongitude());
        dest.setRating(request.getRating());
        dest.setTags(request.getTags());

        destinationMapper.updateById(dest);

        // Update traveler bindings
        travelerDestinationMapper.delete(
                new LambdaQueryWrapper<TravelerDestination>().eq(TravelerDestination::getDestinationId, id));
        bindTravelers(id, request.getOwner());

        return DestinationResponse.from(dest);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @AuditLog(module = "DESTINATION", action = "DELETE", resourceId = "#id")
    public void delete(String id) {
        Destination dest = destinationMapper.selectById(id);
        if (dest == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "目的地不存在");
        }
        travelerDestinationMapper.delete(
                new LambdaQueryWrapper<TravelerDestination>().eq(TravelerDestination::getDestinationId, id));
        destinationMapper.deleteById(id);
    }

    private String generateDestinationId(String name, String owner) {
        // Use name hash + short UUID for unique, URL-safe ID
        String shortUuid = UUID.randomUUID().toString().substring(0, 8);
        // Use CRC-style hash of the name for readability
        int nameHash = Math.abs(name.hashCode()) % 10000;
        return nameHash + "-" + shortUuid;
    }

    private void bindTravelers(String destinationId, String owner) {
        List<String> travelerIds = new ArrayList<>();
        if ("both".equals(owner)) {
            travelerIds.addAll(Arrays.asList("male", "female"));
        } else if ("male".equals(owner) || "female".equals(owner)) {
            travelerIds.add(owner);
        }
        for (String travelerId : travelerIds) {
            TravelerDestination td = new TravelerDestination();
            td.setDestinationId(destinationId);
            td.setTravelerId(travelerId);
            travelerDestinationMapper.insert(td);
        }
    }
}
