package com.travel.module.file.controller;

import com.travel.common.result.R;
import com.travel.module.file.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload")
    public R<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        String url = fileService.upload(file);
        Map<String, String> data = new HashMap<>(2);
        data.put("url", url);
        return R.ok(data);
    }

    @DeleteMapping("/delete")
    public R<Void> delete(@RequestParam("url") String url) {
        fileService.delete(url);
        return R.ok();
    }
}
