package com.simple_cabinet_medical.Backend.Permission;

import com.simple_cabinet_medical.Backend.model.*;
import com.simple_cabinet_medical.Backend.repository.UtilisateurRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {

    private static final Logger logger = LoggerFactory.getLogger(CustomPermissionEvaluator.class);

    private final UtilisateurRepository utilisateurRepository;
    private final EntityFetcher entityFetcher;

    public CustomPermissionEvaluator(UtilisateurRepository utilisateurRepository, EntityFetcher entityFetcher) {
        this.utilisateurRepository = utilisateurRepository;
        this.entityFetcher = entityFetcher;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication == null || targetDomainObject == null || permission == null) {
            logger.warn("Permission denied: null authentication, target, or permission");
            return false;
        }
        return checkPermission((Utilisateur) authentication.getPrincipal(), targetDomainObject, permission);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if (authentication == null || targetId == null || permission == null || targetType == null) {
            logger.warn("Permission denied: null authentication, target, or permission");
            return false;
        }
        Object targetDomainObject = entityFetcher.getEntity(targetType, (Long) targetId);
        return checkPermission((Utilisateur) authentication.getPrincipal(), targetDomainObject, permission);
    }

    private boolean checkPermission(Utilisateur currentUser, Object targetDomainObject, Object permission) {
        EAccessLevel level = parseAccessLevel(permission);
        if (level == null) return false;

        Long idUtilisateur = currentUser.getIdUtilisateur();
        EROLE role = currentUser.getRole();
        Long userClientId = null;

        if (role == null) return false;
        if (role == EROLE.ADMIN) return true;
        if (currentUser.getRole()!=EROLE.ADMIN){
            userClientId = currentUser.getClient().getIdClient();
        }
        if (!(targetDomainObject instanceof BasedObject basedObject)) {
            logger.warn("Target object is not BasedObject: {}", targetDomainObject.getClass().getSimpleName());
            return false;
        }

        Long clientObjectId = basedObject.getClientCreatorId();
        Long idUtilisateurObject = basedObject.getIdUtilisateur();

        // Règles selon le type
        if (targetDomainObject instanceof Patient || targetDomainObject instanceof RendezVous) {
            return userClientId.equals(clientObjectId);
        }
        if (targetDomainObject instanceof Consultation || targetDomainObject instanceof Document || targetDomainObject instanceof Traitement) {
            return switch (level) {
                case UPDATE -> idUtilisateur.equals(idUtilisateurObject);
                case READ -> userClientId.equals(clientObjectId);
                case WRITE -> true;
                default -> false;
            };
        }
        if (targetDomainObject instanceof Utilisateur || targetDomainObject instanceof Client
                || targetDomainObject instanceof ClientConfig || targetDomainObject instanceof Local) {
            return (role.equals(EROLE.MEDECIN) && userClientId.equals(clientObjectId))
                    || (level == EAccessLevel.READ && userClientId.equals(clientObjectId));
        }
        return (role.equals(EROLE.MEDECIN) && userClientId.equals(clientObjectId))
                || (role.equals(EROLE.REMPLACANT) && level == EAccessLevel.READ && userClientId.equals(clientObjectId));
    }

    private EAccessLevel parseAccessLevel(Object permission) {
        if (permission instanceof String s) {
            try {
                return EAccessLevel.valueOf(s);
            } catch (IllegalArgumentException e) {
                logger.warn("Unknown permission: {}", s);
                return null;
            }
        }
        return permission instanceof EAccessLevel e ? e : null;
    }
}
