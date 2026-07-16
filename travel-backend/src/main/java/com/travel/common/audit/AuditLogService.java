package com.travel.common.audit;

public interface AuditLogService {
    void logAsync(AuditLogEntry entry);
}
