package com.simple_cabinet_medical.Backend.dto.audit;

import com.simple_cabinet_medical.Backend.model.AuditLog;

import java.util.Map;

/**
 * Tout ce que le filtre transmet au service d'audit pour une requête.
 */
public record AuditEvent(
        Long clientId,
        String clientName,
        Long userId,
        String username,
        String entityType,
        String entityId,
        AuditLog.ActionType actionType,
        Integer httpStatus,
        String ipAddress,
        // état AVANT l'action (modification / suppression), lu avant l'exécution
        Map<String, Object> oldValue,
        // corps JSON de la requête, nettoyé : sert de repli si l'état en base n'est pas lisible
        Map<String, Object> requestBody,
        // true : relire l'état courant de la ligne en base (lecture, création, modification)
        boolean loadNewValue,
        // si non null, la relecture est restreinte à ce client (null = admin, pas de restriction)
        Long tenantClientId
) {
}