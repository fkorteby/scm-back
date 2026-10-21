package com.simple_cabinet_medical.Backend.service.audit;

import com.simple_cabinet_medical.Backend.dto.audit.AuditEvent;
import com.simple_cabinet_medical.Backend.model.AuditLog;
import com.simple_cabinet_medical.Backend.repository.audit.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final AuditSnapshotService snapshotService;

    /**
     * Écriture async : ne doit JAMAIS ralentir ni faire échouer l'action
     * métier appelante. La relecture de l'état en base se fait ici aussi,
     * donc hors de la requête HTTP. Toute exception est avalée + loguée.
     */
    @Async("auditTaskExecutor")
    public void log(AuditEvent event) {
        try {
            boolean success = event.httpStatus() == null || event.httpStatus() < 400;

            Map<String, Object> oldValue = null;
            Map<String, Object> newValue = null;

            // Une action refusée ou en erreur n'a rien changé : pas de contenu.
            if (success) {
                oldValue = event.oldValue();

                if (event.loadNewValue()) {
                    newValue = loadNewValueSafely(event);
                }

                boolean bodyAllowed = event.actionType() == AuditLog.ActionType.CREATION
                        || event.actionType() == AuditLog.ActionType.MODIFICATION;

                if (newValue == null && bodyAllowed) {
                    newValue = event.requestBody();
                }
            }

            boolean hasDetails = (oldValue != null && !oldValue.isEmpty())
                    || (newValue != null && !newValue.isEmpty());

            AuditLog entry = AuditLog.builder()
                    .clientId(event.clientId())
                    .clientName(event.clientName())
                    .userId(event.userId())
                    .username(event.username())
                    .timestamp(Instant.now())
                    .entityType(event.entityType())
                    .entityId(event.entityId())
                    .actionType(event.actionType())
                    .httpStatus(event.httpStatus())
                    .success(success)
                    .ipAddress(event.ipAddress())
                    .oldValue(oldValue)
                    .newValue(newValue)
                    .hasDetails(hasDetails)
                    .build();

            auditLogRepository.save(entry);

        } catch (Exception e) {
            log.error(
                    "Échec de l'écriture du log d'audit (entityType={}, entityId={}, action={})",
                    event.entityType(), event.entityId(), event.actionType(), e
            );
        }
    }

    // Si la relecture échoue, on garde quand même le log (sans le contenu).
    private Map<String, Object> loadNewValueSafely(AuditEvent event) {
        try {
            return snapshotService.loadRow(event.entityType(), event.entityId(), event.tenantClientId());
        } catch (Exception e) {
            log.warn("Relecture de l'état impossible (entityType={}, entityId={})",
                    event.entityType(), event.entityId(), e);
            return null;
        }
    }
}