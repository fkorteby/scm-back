package com.simple_cabinet_medical.Backend.service.audit;

import com.simple_cabinet_medical.Backend.model.AuditLog;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Décide, pour une requête (méthode + URL), SI on l'audite et SUR QUOI :
 * type d'entité, id de l'objet, type d'action.
 *
 * Les URL particulières de ton application sont décrites une par une ;
 * tout le reste suit la convention Spring Data REST /api/{ressource}/{id}.
 */
@Component
public class AuditTargetResolver {

    /**
     * @param entityType   type d'entité (ex. "patients")
     * @param entityId     id de l'objet, ou null s'il n'est pas dans l'URL (création)
     * @param forcedAction type d'action imposé, ou null pour le déduire de la méthode HTTP
     * @param ignored      true : ne pas auditer cette requête
     * @param bodyAsPayload true : le contenu à afficher est le corps de la requête
     *                      (pas l'état de la ligne en base)
     */
    public record AuditTarget(
            String entityType,
            String entityId,
            AuditLog.ActionType forcedAction,
            boolean ignored,
            boolean bodyAsPayload
    ) {
        static AuditTarget skip() {
            return new AuditTarget(null, null, null, true, false);
        }
    }

    // En lecture, seul le détail de ces ressources est audité (/api/patients/42).
    private static final Set<String> AUDITED_READ_TYPES = Set.of("patients");

    // Simple demande d'URL signées pour l'upload : aucune donnée métier modifiée.
    private static final Pattern UPLOAD_URLS =
            Pattern.compile("^/api(?:/v\\d+)?/consultations/get-upload-urls/\\d+$");

    // Ouverture du détail d'une consultation (le front ne fait jamais de getById).
    // Accepte les deux formes d'URL : .../consultations/{id}/files
    // et .../consultations/consultation/{id}/files
    private static final Pattern CONSULTATION_FILES =
            Pattern.compile("^/api(?:/v\\d+)?/consultations/(?:consultation/)?(\\d+)/files$");

    // Suppression d'une consultation (contrôleur custom).
    private static final Pattern CONSULTATION_DELETE_V2 =
            Pattern.compile("^/api(?:/v\\d+)?/consultations/V2/(\\d+)$");

    // Mise à jour des fichiers d'une consultation (POST mais c'est une modification).
    private static final Pattern CONSULTATION_UPDATE =
            Pattern.compile("^/api(?:/v\\d+)?/consultations/update/(\\d+)$");

    // Convention Spring Data REST : ressource précise /api/{type}/{id}
    private static final Pattern ITEM =
            Pattern.compile("^/api(?:/v\\d+)?/([^/]+)/(\\d+)$");

    // Collection /api/{type} (liste en GET, création en POST)
    private static final Pattern COLLECTION =
            Pattern.compile("^/api(?:/v\\d+)?/([^/]+)$");

    public AuditTarget resolve(String method, String uri) {

        String path = uri.replaceAll("/+$", "");
        boolean isGet = "GET".equals(method);

        if ("POST".equals(method) && UPLOAD_URLS.matcher(path).matches()) {
            return AuditTarget.skip();
        }

        Matcher m = CONSULTATION_FILES.matcher(path);
        if (isGet && m.matches()) {
            return new AuditTarget("consultations", m.group(1), AuditLog.ActionType.LECTURE, false, false);
        }

        m = CONSULTATION_DELETE_V2.matcher(path);
        if ("DELETE".equals(method) && m.matches()) {
            return new AuditTarget("consultations", m.group(1), AuditLog.ActionType.SUPPRESSION, false, false);
        }

        m = CONSULTATION_UPDATE.matcher(path);
        if ("POST".equals(method) && m.matches()) {
            return new AuditTarget("consultations", m.group(1), AuditLog.ActionType.MODIFICATION, false, true);
        }

        m = ITEM.matcher(path);
        if (m.matches()) {
            String type = m.group(1);
            if (isGet && !AUDITED_READ_TYPES.contains(type.toLowerCase())) {
                return AuditTarget.skip();
            }
            return new AuditTarget(type, m.group(2), null, false, false);
        }

        // Toute autre lecture (listes, recherches, sous-ressources) : non auditée.
        if (isGet) {
            return AuditTarget.skip();
        }

        m = COLLECTION.matcher(path);
        if (m.matches()) {
            return new AuditTarget(m.group(1), null, null, false, false);
        }

        // Autre écriture : on l'audite quand même, sous son premier segment d'URL.
        String[] parts = path.replaceFirst("^/api(?:/v\\d+)?/?", "").split("/");
        String type = parts.length > 0 && !parts[0].isBlank() ? parts[0] : path;
        return new AuditTarget(type, null, null, false, false);
    }
}