package com.simple_cabinet_medical.Backend.repository.audit;

import com.simple_cabinet_medical.Backend.dto.audit.AuditFilterOptions;
import com.simple_cabinet_medical.Backend.dto.audit.AuditLogSearchCriteria;
import com.simple_cabinet_medical.Backend.model.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuditLogRepositoryCustom {

    Page<AuditLog> search(AuditLogSearchCriteria criteria, Pageable pageable);

    /**
     * @param clientsScope     si non null, la liste des clients est limitée à ce client ;
     *                         null = tous les clients (super admin)
     * @param effectiveClientId si non null, les utilisateurs et types d'entité proposés
     *                         sont limités à ce client
     */
    AuditFilterOptions findFilterOptions(Long clientsScope, Long effectiveClientId);
}