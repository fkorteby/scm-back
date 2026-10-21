package com.simple_cabinet_medical.Backend.service.audit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Lit l'état d'une ligne directement en base (JDBC) et le transforme en
 * Map prête à être stockée dans Mongo. Pas de JPA : pas de lazy loading,
 * pas de cycles, et aucune interception de sécurité sur les repositories.
 *
 * Gère aussi le nettoyage du corps JSON des requêtes (mots de passe masqués).
 */
@Component
@RequiredArgsConstructor
public class AuditSnapshotService {

    /**
     * @param tenantColumn colonne "client propriétaire" (null si absente)
     */
    public record EntityInfo(String table, String idColumn, String tenantColumn) {
    }

    /*
     * Entités dont on sait relire l'état en base. La clé est le nom de la
     * ressource dans l'URL (/api/patients/42 -> "patients").
     *
     * ⚠️ À VÉRIFIER / COMPLÉTER : noms de tables et de colonnes. Pour une
     * entité absente de cette liste, on garde uniquement le corps de la
     * requête (création / modification) et rien pour une suppression.
     */
    private static final Map<String, EntityInfo> ENTITIES = Map.of(
            "patients", new EntityInfo("patient", "id_patient", "client_creator_id"),
            "consultations", new EntityInfo("consultation", "id_consultation", "client_creator_id"),
            "medicaments", new EntityInfo("medicament", "id_medicament", "client_creator_id")
    );

    // Toute clé qui ressemble à un secret n'est jamais stockée en clair.
    private static final Pattern SENSITIVE_KEY =
            Pattern.compile("(?i).*(password|passwd|motdepasse|mot_de_passe|mdp|token|secret).*");

    private static final String MASK = "***";

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public boolean isRegistered(String entityType) {
        return entityType != null && ENTITIES.containsKey(entityType.toLowerCase());
    }

    /**
     * Relit une ligne. Retourne null si l'entité n'est pas gérée, si l'id
     * n'est pas numérique, ou si la ligne n'existe pas (ou n'appartient pas
     * au client demandé).
     */
    public Map<String, Object> loadRow(String entityType, String entityId, Long tenantClientId) {

        if (!isRegistered(entityType) || entityId == null) {
            return null;
        }

        long id;
        try {
            id = Long.parseLong(entityId);
        } catch (NumberFormatException e) {
            return null;
        }

        EntityInfo info = ENTITIES.get(entityType.toLowerCase());

        // Table et colonnes viennent de la liste ci-dessus (jamais d'une
        // entrée utilisateur) ; seul l'id est un paramètre lié.
        StringBuilder sql = new StringBuilder("SELECT * FROM ")
                .append(info.table())
                .append(" WHERE ")
                .append(info.idColumn())
                .append(" = ?");

        List<Object> args = new ArrayList<>();
        args.add(id);

        if (tenantClientId != null && info.tenantColumn() != null) {
            sql.append(" AND ").append(info.tenantColumn()).append(" = ?");
            args.add(tenantClientId);
        }

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql.toString(), args.toArray());

        return rows.isEmpty() ? null : sanitizeRow(rows.get(0));
    }

    // =========================================================
    // LIGNE SQL -> MAP
    // =========================================================

    private Map<String, Object> sanitizeRow(Map<String, Object> row) {
        Map<String, Object> out = new LinkedHashMap<>();

        for (Map.Entry<String, Object> entry : row.entrySet()) {
            String key = cleanKey(entry.getKey());
            out.put(key, SENSITIVE_KEY.matcher(key).matches() ? MASK : normalize(entry.getValue()));
        }

        return out;
    }

    // Ne garde que des types que Mongo et Jackson savent traiter sans surprise.
    private Object normalize(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof String || value instanceof Boolean
                || value instanceof Integer || value instanceof Long || value instanceof Double) {
            return value;
        }
        if (value instanceof java.sql.Timestamp ts) {
            return ts.toInstant().toString();
        }
        if (value instanceof java.sql.Date d) {
            return d.toLocalDate().toString();
        }
        if (value instanceof byte[] bytes) {
            return "[binaire " + bytes.length + " octets]";
        }
        return value.toString();
    }

    // =========================================================
    // CORPS JSON DE REQUÊTE -> MAP
    // =========================================================

    public Map<String, Object> bodyToMap(byte[] content, int maxBytes) {

        if (content == null || content.length == 0) {
            return null;
        }

        if (content.length >= maxBytes) {
            Map<String, Object> info = new LinkedHashMap<>();
            info.put("_info", "Corps de requête trop volumineux : non enregistré");
            return info;
        }

        try {
            JsonNode root = objectMapper.readTree(content);

            if (root.isObject()) {
                return cleanObject(root);
            }

            Map<String, Object> wrapper = new LinkedHashMap<>();
            wrapper.put("body", cleanValue(root));
            return wrapper;

        } catch (Exception e) {
            return null;
        }
    }

    private Map<String, Object> cleanObject(JsonNode node) {
        Map<String, Object> out = new LinkedHashMap<>();

        Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            String key = cleanKey(field.getKey());
            out.put(key, SENSITIVE_KEY.matcher(key).matches() ? MASK : cleanValue(field.getValue()));
        }

        return out;
    }

    private Object cleanValue(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        if (node.isObject()) {
            return cleanObject(node);
        }
        if (node.isArray()) {
            List<Object> list = new ArrayList<>();
            node.forEach(item -> list.add(cleanValue(item)));
            return list;
        }
        if (node.isBoolean()) {
            return node.asBoolean();
        }
        if (node.isIntegralNumber()) {
            return node.asLong();
        }
        if (node.isNumber()) {
            return node.asDouble();
        }
        return node.asText();
    }

    // Mongo n'accepte pas de point dans les clés d'une Map.
    private String cleanKey(String key) {
        if (key == null) {
            return "_";
        }
        String cleaned = key.replace('.', '_');
        return cleaned.startsWith("$") ? "_" + cleaned.substring(1) : cleaned;
    }
}