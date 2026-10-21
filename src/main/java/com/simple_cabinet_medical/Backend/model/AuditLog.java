package com.simple_cabinet_medical.Backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "audit_logs")
@CompoundIndexes({
        // Pattern de requête principal : filtré par client, trié par date desc
        @CompoundIndex(name = "client_timestamp_idx", def = "{'clientId': 1, 'timestamp': -1}")
})
public class AuditLog {

    @Id
    private String id;

    @Indexed
    private Long clientId;

    private String clientName;

    @Indexed
    private Long userId;

    @Indexed
    private String username;

    @Indexed
    private Instant timestamp;

    @Indexed
    private String entityType;

    private String entityId;

    @Indexed
    private ActionType actionType;

    private Integer httpStatus;

    @Indexed
    private boolean success;

    private String ipAddress;

    /*
     * Contenu de l'action :
     *  - LECTURE      -> newValue = l'objet consulté
     *  - CREATION     -> newValue = l'objet ajouté
     *  - MODIFICATION -> oldValue = état avant, newValue = état après
     *  - SUPPRESSION  -> oldValue = l'objet supprimé
     *
     * Ces champs ne sont PAS renvoyés par la liste (trop lourds) :
     * on les récupère via GET /api/audit-logs/{id}.
     */
    private Map<String, Object> oldValue;

    private Map<String, Object> newValue;

    // true si oldValue ou newValue est renseigné (sert à afficher l'icône détail)
    private Boolean hasDetails;

    public enum ActionType {
        LECTURE,
        CREATION,
        MODIFICATION,
        CHANGEMENT_STATUT,
        SUPPRESSION
    }
}