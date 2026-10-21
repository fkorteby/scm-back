package com.simple_cabinet_medical.Backend.service.audit;

import com.simple_cabinet_medical.Backend.dto.audit.AuditFilterOptions;
import com.simple_cabinet_medical.Backend.dto.audit.AuditLogSearchCriteria;
import com.simple_cabinet_medical.Backend.model.AuditLog;
import com.simple_cabinet_medical.Backend.repository.audit.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuditLogQueryService {

    private static final int MAX_PAGE_SIZE = 200;

    private final AuditLogRepository auditLogRepository;

    public Page<AuditLog> search(AuditLogSearchCriteria criteria, Pageable pageable) {
        return auditLogRepository.search(criteria, clampPageSize(pageable));
    }

    public Optional<AuditLog> findById(String id) {
        return auditLogRepository.findById(id);
    }

    public AuditFilterOptions getFilterOptions(Long clientsScope, Long effectiveClientId) {
        return auditLogRepository.findFilterOptions(clientsScope, effectiveClientId);
    }

    private Pageable clampPageSize(Pageable pageable) {
        if (pageable.getPageSize() > MAX_PAGE_SIZE) {
            return PageRequest.of(pageable.getPageNumber(), MAX_PAGE_SIZE, pageable.getSort());
        }
        return pageable;
    }
}