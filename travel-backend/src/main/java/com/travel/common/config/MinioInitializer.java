package com.travel.common.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.SetBucketPolicyArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
@RequiredArgsConstructor
public class MinioInitializer {

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    /**
     * 启动时检查并初始化 bucket：不存在则创建，并设置公开读策略。
     * 前端可直接通过 URL 读取图片（只读），上传/删除必须通过后端 API（需认证）。
     */
    @PostConstruct
    public void init() {
        try {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(minioConfig.getBucket()).build());
            if (!exists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder().bucket(minioConfig.getBucket()).build());
                log.info("MinIO bucket [{}] 创建成功", minioConfig.getBucket());
            } else {
                log.info("MinIO bucket [{}] 已存在，跳过创建", minioConfig.getBucket());
            }

            String policy = "{\n" +
                    "  \"Version\": \"2012-10-17\",\n" +
                    "  \"Statement\": [\n" +
                    "    {\n" +
                    "      \"Effect\": \"Allow\",\n" +
                    "      \"Principal\": {\"AWS\": [\"*\"]},\n" +
                    "      \"Action\": [\"s3:GetObject\"],\n" +
                    "      \"Resource\": [\"arn:aws:s3:::" + minioConfig.getBucket() + "/*\"]\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";
            minioClient.setBucketPolicy(
                    SetBucketPolicyArgs.builder()
                            .bucket(minioConfig.getBucket())
                            .config(policy)
                            .build());
            log.info("MinIO bucket [{}] 公开读策略已设置", minioConfig.getBucket());
        } catch (Exception e) {
            log.error("MinIO bucket 初始化失败", e);
            throw new RuntimeException("MinIO bucket 初始化失败", e);
        }
    }
}
