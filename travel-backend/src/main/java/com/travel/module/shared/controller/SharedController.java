package com.travel.module.shared.controller;

import com.travel.common.result.R;
import com.travel.module.shared.dto.SharedResponse;
import com.travel.module.shared.service.SharedService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/shared")
@RequiredArgsConstructor
public class SharedController {

    private final SharedService sharedService;

    @GetMapping
    public R<List<SharedResponse>> getShared() {
        List<SharedResponse> shared = sharedService.getSharedFootprints();
        return R.ok(shared);
    }
}
