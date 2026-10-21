package com.simple_cabinet_medical.Backend.dto.audit;

import com.simple_cabinet_medical.Backend.model.AuditLog;

import java.time.Instant;

public record AuditLogSearchCriteria(
        Long clientId,
        Long userId,
        String entityType,
        AuditLog.ActionType actionType,
        Boolean success,
        Instant from,
        Instant to
) {
}