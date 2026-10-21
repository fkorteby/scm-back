package com.simple_cabinet_medical.Backend.controller.audit;

import com.simple_cabinet_medical.Backend.dto.audit.AuditFilterOptions;
import com.simple_cabinet_medical.Backend.dto.audit.AuditLogSearchCriteria;
import com.simple_cabinet_medical.Backend.model.AuditLog;
import com.simple_cabinet_medical.Backend.service.audit.AuditLogQueryService;
import com.simple_cabinet_medical.Backend.service.audit.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

/**
 * Consultation des logs d'audit : RÉSERVÉ AUX ADMINS.
 * L'admin voit les logs de tous les clients et filtre comme il veut.
 */
@RestController
@RequestMapping("/api/v1/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogQueryService auditLogQueryService;
    private final CurrentUserProvider currentUserProvider;

    @GetMapping
    public Page<AuditLog> getAll(
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) AuditLog.ActionType actionType,
            @RequestParam(required = false) Boolean success,
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to,
            @PageableDefault(size = 20, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        requireAdmin();

        AuditLogSearchCriteria criteria = new AuditLogSearchCriteria(
                clientId, userId, entityType, actionType, success, from, to
        );

        return auditLogQueryService.search(criteria, pageable);
    }

    /**
     * Valeurs des listes déroulantes de filtres : tous les clients ; les
     * utilisateurs et types d'entité se limitent au client choisi s'il y en a un.
     */
    @GetMapping("/filter-options")
    public AuditFilterOptions getFilterOptions(@RequestParam(required = false) Long clientId) {
        requireAdmin();
        return auditLogQueryService.getFilterOptions(null, clientId);
    }

    /**
     * Détail complet d'un log, avec le contenu (oldValue / newValue).
     */
    @GetMapping("/{id}")
    public AuditLog getById(@PathVariable String id) {
        requireAdmin();

        return auditLogQueryService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    private void requireAdmin() {
        if (!currentUserProvider.getCurrentUser().superAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }
}