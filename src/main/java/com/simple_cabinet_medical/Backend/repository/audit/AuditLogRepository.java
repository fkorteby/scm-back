package com.simple_cabinet_medical.Backend.repository.audit;

import com.simple_cabinet_medical.Backend.model.AuditLog;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface AuditLogRepository
        extends MongoRepository<AuditLog, String>, AuditLogRepositoryCustom {
}
