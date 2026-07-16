package com.travel.module.destination.controller;

import com.travel.common.result.R;
import com.travel.module.destination.dto.DestinationRequest;
import com.travel.module.destination.dto.DestinationResponse;
import com.travel.module.destination.service.DestinationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/destinations")
@RequiredArgsConstructor
public class DestinationController {

    private final DestinationService destinationService;

    @GetMapping
    public R<List<DestinationResponse>> list(@RequestParam(required = false) String region) {
        List<DestinationResponse> list = destinationService.listAll(region);
        return R.ok(list);
    }

    @GetMapping("/{id}")
    public R<DestinationResponse> getById(@PathVariable String id) {
        DestinationResponse dest = destinationService.getById(id);
        return R.ok(dest);
    }

    @PostMapping
    public R<DestinationResponse> create(@Valid @RequestBody DestinationRequest request) {
        DestinationResponse dest = destinationService.create(request);
        return R.ok(dest);
    }

    @PutMapping("/{id}")
    public R<DestinationResponse> update(@PathVariable String id, @Valid @RequestBody DestinationRequest request) {
        DestinationResponse dest = destinationService.update(id, request);
        return R.ok(dest);
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable String id) {
        destinationService.delete(id);
        return R.ok();
    }
}
