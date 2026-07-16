package com.travel.module.traveler.controller;

import com.travel.common.result.R;
import com.travel.module.traveler.dto.TravelerResponse;
import com.travel.module.traveler.service.TravelerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/travelers")
@RequiredArgsConstructor
public class TravelerController {

    private final TravelerService travelerService;

    @GetMapping("/{travelerId}")
    public R<TravelerResponse> getTraveler(@PathVariable String travelerId) {
        TravelerResponse traveler = travelerService.getTravelerProfile(travelerId);
        return R.ok(traveler);
    }
}
