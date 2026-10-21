package com.simple_cabinet_medical.Backend.config.audit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simple_cabinet_medical.Backend.dto.audit.AuditEvent;
import com.simple_cabinet_medical.Backend.model.AuditLog;
import com.simple_cabinet_medical.Backend.service.audit.AuditService;
import com.simple_cabinet_medical.Backend.service.audit.AuditSnapshotService;
import com.simple_cabinet_medical.Backend.service.audit.AuditTargetResolver;
import com.simple_cabinet_medical.Backend.service.audit.CurrentUserProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AuditFilter extends OncePerRequestFilter {

    private static final int MAX_BODY_BYTES = 256 * 1024;

    private final AuditService auditService;
    private final CurrentUserProvider currentUserProvider;
    private final AuditTargetResolver targetResolver;
    private final AuditSnapshotService snapshotService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String uri = request.getRequestURI();
        String method = request.getMethod();

        // Hors API, ou routes d'authentification : on ne touche à rien.
        if (!uri.startsWith("/api") || uri.contains("/auth/currentUser") || uri.contains("/auth/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        AuditTargetResolver.AuditTarget target = targetResolver.resolve(method, uri);

        // Requête non auditée (listes, recherches, upload d'URL...) : aucun
        // wrapper, aucune surcharge.
        if (target.ignored()) {
            filterChain.doFilter(request, response);
            return;
        }

        AuditLog.ActionType action = target.forcedAction() != null
                ? target.forcedAction()
                : mapMethodToActionType(method);

        HttpServletRequest requestToUse = shouldCaptureBody(request, method)
                ? new ContentCachingRequestWrapper(request, MAX_BODY_BYTES)
                : request;

        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        Map<String, Object> oldValue = loadPreImage(request, target, action);

        try {
            filterChain.doFilter(requestToUse, responseWrapper);
        } finally {
            record(request, requestToUse, responseWrapper, target, action, oldValue);
            // Très important : restituer le contenu au client
            responseWrapper.copyBodyToResponse();
        }
    }

    // =========================================================
    // ÉTAT D'ORIGINE (avant exécution)
    // =========================================================

    /*
     * Lu AVANT la requête, sinon il est trop tard (objet modifié ou supprimé).
     * Comme ce filtre passe avant l'authentification, on exige au moins un
     * jeton Bearer, et le résultat n'est conservé que si la requête réussit
     * (donc si l'utilisateur était bien autorisé).
     */
    private Map<String, Object> loadPreImage(
            HttpServletRequest request,
            AuditTargetResolver.AuditTarget target,
            AuditLog.ActionType action
    ) {
        boolean needsPreImage = action == AuditLog.ActionType.MODIFICATION
                || action == AuditLog.ActionType.SUPPRESSION;

        if (!needsPreImage || target.bodyAsPayload() || target.entityId() == null) {
            return null;
        }

        if (!snapshotService.isRegistered(target.entityType())) {
            return null;
        }

        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }

        try {
            return snapshotService.loadRow(target.entityType(), target.entityId(), null);
        } catch (Exception e) {
            return null;
        }
    }

    // =========================================================
    // ENREGISTREMENT (après exécution)
    // =========================================================

    private void record(
            HttpServletRequest request,
            HttpServletRequest usedRequest,
            ContentCachingResponseWrapper response,
            AuditTargetResolver.AuditTarget target,
            AuditLog.ActionType action,
            Map<String, Object> oldValue
    ) {
        try {
            int status = response.getStatus();
            boolean success = status < 400;

            String entityId = target.entityId();
            if (entityId == null && action == AuditLog.ActionType.CREATION) {
                entityId = extractCreatedId(response);
            }

            CurrentUserProvider.CurrentUser user = currentUserProvider.getCurrentUser();

            // Corps de la requête : repli quand l'état en base n'est pas lisible
            Map<String, Object> body = null;
            boolean bodyUseful = action == AuditLog.ActionType.CREATION
                    || action == AuditLog.ActionType.MODIFICATION;

            if (success && bodyUseful && usedRequest instanceof ContentCachingRequestWrapper wrapper) {
                body = snapshotService.bodyToMap(wrapper.getContentAsByteArray(), MAX_BODY_BYTES);
            }

            boolean loadNewValue = success
                    && !target.bodyAsPayload()
                    && entityId != null
                    && action != AuditLog.ActionType.SUPPRESSION
                    && snapshotService.isRegistered(target.entityType());

            // Un utilisateur non admin ne peut jamais relire la ligne d'un autre client.
            Long tenantClientId = user.superAdmin() ? null : user.clientId();

            auditService.log(new AuditEvent(
                    user.clientId(),
                    user.clientName(),
                    user.userId(),
                    user.username(),
                    target.entityType(),
                    entityId,
                    action,
                    status,
                    request.getRemoteAddr(),
                    success ? oldValue : null,
                    body,
                    loadNewValue,
                    tenantClientId
            ));

        } catch (Exception e) {
            // L'audit ne doit jamais faire remonter d'erreur au client.
        }
    }

    // =========================================================
    // UTILITAIRES
    // =========================================================

    private boolean shouldCaptureBody(HttpServletRequest request, String method) {
        if (!"POST".equals(method) && !"PUT".equals(method) && !"PATCH".equals(method)) {
            return false;
        }
        String contentType = request.getContentType();
        return contentType != null && contentType.toLowerCase().contains("json");
    }

    private AuditLog.ActionType mapMethodToActionType(String method) {
        return switch (method) {
            case "GET" -> AuditLog.ActionType.LECTURE;
            case "POST" -> AuditLog.ActionType.CREATION;
            case "PUT", "PATCH" -> AuditLog.ActionType.MODIFICATION;
            case "DELETE" -> AuditLog.ActionType.SUPPRESSION;
            default -> AuditLog.ActionType.LECTURE;
        };
    }

    // Création : l'id vient du header Location, sinon du JSON de la réponse.
    private String extractCreatedId(ContentCachingResponseWrapper response) {

        String fromLocation = lastPathSegment(response.getHeader("Location"));
        if (fromLocation != null) {
            return fromLocation;
        }

        try {
            byte[] content = response.getContentAsByteArray();
            if (content.length > 0) {
                return findIdField(objectMapper.readTree(content));
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    /**
     * Dernier segment de chemin d'une URI, relative ou absolue
     * ("http://host:8080/api/patients/42" -> "42"). Null si absent.
     */
    private String lastPathSegment(String uri) {
        if (uri == null || uri.isBlank()) {
            return null;
        }
        String trimmed = uri.replaceAll("/+$", "");
        int idx = trimmed.lastIndexOf('/');
        if (idx == -1 || idx == trimmed.length() - 1) {
            return null;
        }
        String last = trimmed.substring(idx + 1);
        return last.isBlank() ? null : last;
    }

    /**
     * Cherche un champ id dans le JSON racine : "id" ou "id<Xxx>"
     * (idPatient, idConsultation...).
     */
    private String findIdField(JsonNode node) {
        if (node == null || !node.isObject()) {
            return null;
        }

        if (node.has("id") && isScalar(node.get("id"))) {
            return node.get("id").asText();
        }

        Iterator<String> fieldNames = node.fieldNames();
        while (fieldNames.hasNext()) {
            String field = fieldNames.next();

            boolean looksLikeId = field.length() > 2
                    && field.startsWith("id")
                    && Character.isUpperCase(field.charAt(2));

            if (looksLikeId && isScalar(node.get(field))) {
                return node.get(field).asText();
            }
        }

        return null;
    }

    private boolean isScalar(JsonNode value) {
        return value != null && !value.isNull() && (value.isNumber() || value.isTextual());
    }
}