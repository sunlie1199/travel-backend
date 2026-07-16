package com.travel.module.stats.controller;

import com.travel.common.result.R;
import com.travel.module.stats.dto.StatsResponse;
import com.travel.module.stats.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @GetMapping
    public R<StatsResponse> getStats() {
        StatsResponse stats = statsService.getStats();
        return R.ok(stats);
    }
}
