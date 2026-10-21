package com.simple_cabinet_medical.Backend.repository.audit;

import com.simple_cabinet_medical.Backend.dto.audit.AuditFilterOptions;
import com.simple_cabinet_medical.Backend.dto.audit.AuditLogSearchCriteria;
import com.simple_cabinet_medical.Backend.model.AuditLog;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/*
 * IMPORTANT : le nom de cette classe doit être exactement
 * <NomDuRepository>Impl pour que Spring Data la fusionne avec
 * AuditLogRepository.
 */
@RequiredArgsConstructor
public class AuditLogRepositoryImpl implements AuditLogRepositoryCustom {

    private static final String COLLECTION = "audit_logs";

    private final MongoTemplate mongoTemplate;

    // =========================================================
    // RECHERCHE PAGINÉE
    // =========================================================

    @Override
    public Page<AuditLog> search(AuditLogSearchCriteria criteria, Pageable pageable) {

        Query query = buildQuery(criteria);

        long total = mongoTemplate.count(query, AuditLog.class);

        // La liste n'a pas besoin du contenu des objets (lourd) : il se
        // charge à la demande via GET /api/audit-logs/{id}.
        query.fields().exclude("oldValue").exclude("newValue");

        query.with(pageable);
        List<AuditLog> content = mongoTemplate.find(query, AuditLog.class);

        return new PageImpl<>(content, pageable, total);
    }

    private Query buildQuery(AuditLogSearchCriteria criteria) {

        List<Criteria> list = new ArrayList<>();

        if (criteria.clientId() != null) {
            list.add(Criteria.where("clientId").is(criteria.clientId()));
        }

        if (criteria.userId() != null) {
            list.add(Criteria.where("userId").is(criteria.userId()));
        }

        if (criteria.entityType() != null && !criteria.entityType().isBlank()) {
            list.add(Criteria.where("entityType").is(criteria.entityType()));
        }

        if (criteria.actionType() != null) {
            list.add(Criteria.where("actionType").is(criteria.actionType()));
        }

        if (criteria.success() != null) {
            list.add(Criteria.where("success").is(criteria.success()));
        }

        if (criteria.from() != null || criteria.to() != null) {
            Criteria date = Criteria.where("timestamp");
            if (criteria.from() != null) {
                date = date.gte(criteria.from());
            }
            if (criteria.to() != null) {
                date = date.lte(criteria.to());
            }
            list.add(date);
        }

        Query query = new Query();

        if (!list.isEmpty()) {
            query.addCriteria(all(list));
        }

        return query;
    }

    // =========================================================
    // OPTIONS DES FILTRES (clients, utilisateurs, entités)
    // =========================================================

    @Override
    public AuditFilterOptions findFilterOptions(Long clientsScope, Long effectiveClientId) {
        return new AuditFilterOptions(
                findClientOptions(clientsScope),
                findUserOptions(effectiveClientId),
                findEntityTypes(effectiveClientId)
        );
    }

    private List<AuditFilterOptions.ClientOption> findClientOptions(Long clientsScope) {

        Criteria match = clientsScope != null
                ? Criteria.where("clientId").is(clientsScope)
                : Criteria.where("clientId").ne(null);

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(match),
                Aggregation.group("clientId", "clientName")
        );

        Map<Long, AuditFilterOptions.ClientOption> byId = new LinkedHashMap<>();

        for (Document doc : mongoTemplate.aggregate(aggregation, COLLECTION, Document.class).getMappedResults()) {
            Document id = groupId(doc);
            Long clientId = toLong(id.get("clientId"));

            if (clientId != null) {
                byId.putIfAbsent(clientId,
                        new AuditFilterOptions.ClientOption(clientId, (String) id.get("clientName")));
            }
        }

        return byId.values().stream()
                .sorted(Comparator.comparing(
                        AuditFilterOptions.ClientOption::name,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .toList();
    }

    private List<AuditFilterOptions.UserOption> findUserOptions(Long effectiveClientId) {

        List<Criteria> list = new ArrayList<>();
        list.add(Criteria.where("userId").ne(null));
        if (effectiveClientId != null) {
            list.add(Criteria.where("clientId").is(effectiveClientId));
        }

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(all(list)),
                Aggregation.group("userId", "username", "clientId")
        );

        Map<Long, AuditFilterOptions.UserOption> byId = new LinkedHashMap<>();

        for (Document doc : mongoTemplate.aggregate(aggregation, COLLECTION, Document.class).getMappedResults()) {
            Document id = groupId(doc);
            Long userId = toLong(id.get("userId"));

            if (userId != null) {
                byId.putIfAbsent(userId, new AuditFilterOptions.UserOption(
                        userId,
                        (String) id.get("username"),
                        toLong(id.get("clientId"))
                ));
            }
        }

        return byId.values().stream()
                .sorted(Comparator.comparing(
                        AuditFilterOptions.UserOption::username,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .toList();
    }

    private List<String> findEntityTypes(Long effectiveClientId) {

        Query query = new Query();
        if (effectiveClientId != null) {
            query.addCriteria(Criteria.where("clientId").is(effectiveClientId));
        }

        return mongoTemplate.findDistinct(query, "entityType", AuditLog.class, String.class)
                .stream()
                .filter(Objects::nonNull)
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();
    }

    // =========================================================
    // UTILITAIRES
    // =========================================================

    private Criteria all(List<Criteria> list) {
        return new Criteria().andOperator(list.toArray(new Criteria[0]));
    }

    private Document groupId(Document doc) {
        Object id = doc.get("_id");
        return id instanceof Document d ? d : new Document();
    }

    private Long toLong(Object value) {
        return value instanceof Number n ? n.longValue() : null;
    }
}