package com.travel.module.file.service.impl;

import com.travel.common.audit.AuditLog;
import com.travel.common.config.MinioConfig;
import com.travel.module.file.service.FileService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    @Override
    @AuditLog(module = "FILE", action = "UPLOAD",
              resourceName = "#file.originalFilename", detail = "#result")
    public String upload(MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String objectName = "travel/" + UUID.randomUUID() + extension;

            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minioConfig.getBucket())
                    .object(objectName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());

            String url = minioConfig.getEndpoint() + "/" + minioConfig.getBucket() + "/" + objectName;
            log.info("文件上传成功: {}", url);
            return url;
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    @AuditLog(module = "FILE", action = "DELETE_FILE",
              resourceId = "#url", resourceName = "#url")
    public void delete(String url) {
        try {
            // URL 格式: http://localhost:9000/travel-images/travel/uuid.jpg
            // objectName: travel/uuid.jpg
            String prefix = minioConfig.getEndpoint() + "/" + minioConfig.getBucket() + "/";
            if (url == null || !url.startsWith(prefix)) {
                throw new IllegalArgumentException("非法的文件 URL: " + url);
            }
            String objectName = url.substring(prefix.length());
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(minioConfig.getBucket())
                    .object(objectName)
                    .build());
            log.info("文件删除成功: {}", objectName);
        } catch (Exception e) {
            log.error("文件删除失败: {}", url, e);
            throw new RuntimeException("文件删除失败: " + e.getMessage());
        }
    }
}
