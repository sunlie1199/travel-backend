package com.travel.common.audit;

import com.travel.common.audit.mapper.AuditLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogMapper auditLogMapper;

    @Override
    @Async("auditExecutor")
    public void logAsync(AuditLogEntry entry) {
        try {
            auditLogMapper.insert(entry);
        } catch (Exception e) {
            log.error("审计日志写入失败", e);
        }
    }
}
