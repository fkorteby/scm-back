package com.simple_cabinet_medical.Backend.Permission;

import com.simple_cabinet_medical.Backend.model.*;
import com.simple_cabinet_medical.Backend.repository.UtilisateurRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

import static com.simple_cabinet_medical.Backend.model.EAccessLevel.*;

@Component
@Primary
public class CustomPermissionEvaluator implements PermissionEvaluator {

    private final UtilisateurRepository utilisateurRepository;
    private final EntityFetcher entityFetcher;

    public CustomPermissionEvaluator(UtilisateurRepository utilisateurRepository, EntityFetcher entityFetcher) {
        this.utilisateurRepository = utilisateurRepository;
        this.entityFetcher = entityFetcher;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication == null || targetDomainObject == null || permission == null) {
            return false;
        }

        Utilisateur currentUser = (Utilisateur) authentication.getPrincipal();
        EROLE role = currentUser.getRole();

        if (role == null) {
            return false;
        }

        switch (role) {
            case ADMIN:
                return true;

            case MEDECIN:
                return handleMedecinPermissions(currentUser, targetDomainObject, permission);

            case REMPLACANT:
                return handleRemplacantPermissions(currentUser, targetDomainObject, permission);

            case SECRETAIRE:
                return handleSecretairePermissions(currentUser, targetDomainObject, permission);

            default:
                return false;
        }
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if (authentication == null || targetId == null || targetType == null || permission == null) {
            return false;
        }
        Utilisateur currentUser = (Utilisateur) authentication.getPrincipal();
        EROLE role = currentUser.getRole();
        Object targetDomainObject = entityFetcher.getEntity(targetType, (Long) targetId);

        if (role == null) {
            return false;
        }
        switch (role) {
            case ADMIN:
                return true;

            case MEDECIN:
                return handleMedecinPermissions(currentUser, targetDomainObject, permission);

            case REMPLACANT:
                return handleRemplacantPermissions(currentUser, targetDomainObject, permission);

            case SECRETAIRE:
                return handleSecretairePermissions(currentUser, targetDomainObject, permission);

            default:
                return false;
        }
    }

    private boolean handleMedecinPermissions(Utilisateur user, Object target, Object permission) {
        if (permission.equals(READ.toString())) {
            return handleReadAccess(user, target);
        } else if (permission.equals(WRITE.toString()) || permission.equals(UPDATE.toString()) || permission.equals(DELETE.toString())) {
            return handleWriteAccessForMedecin(user, target);
        }
        return false;
    }

    private boolean handleRemplacantPermissions(Utilisateur user, Object target, Object permission) {

        if (permission.equals(READ.toString())) {
            return handleReadAccess(user, target);
        } else if (permission.equals(DELETE.toString()) || permission.equals(UPDATE.toString())) {
            if (target instanceof Patient) {
                return true;
            }
            return isSameUser(user, getUtilisateurByTarget(target).getIdUtilisateur());
        }
        return false;
    }

    private boolean handleSecretairePermissions(Utilisateur user, Object target, Object permission) {

        if (permission.equals(READ.toString())) {
            return isSameClient(user, getUtilisateurByTarget(target).getIdUtilisateur());
        } else if (permission.equals(WRITE.toString()) || permission.equals(DELETE.toString()) || permission.equals(UPDATE.toString())) {
            if (target instanceof Patient) {
                return true;
            }
        }
        return false;
    }

    private boolean handleReadAccess(Utilisateur user, Object target) {
        if (target instanceof Client) {
            return isSameClient(user, ((Client) target).getIdUtilisateur());
        } else if (target instanceof BasedObject) {
            return isSameClient(user, ((BasedObject) target).getIdUtilisateur());
        }
        return false;
    }

    private boolean handleWriteAccessForMedecin(Utilisateur user, Object target) {

        if (target instanceof Ordonnance || target instanceof Consultation
                || target instanceof Traitement || target instanceof Document) {

            Utilisateur targetUser = getUtilisateurByTarget(target);
            if (targetUser == null) {
                return false;
            }
            if (targetUser.getRole() == EROLE.MEDECIN) {
                return isSameUser(user, targetUser.getIdUtilisateur());
            } else if (targetUser.getRole() == EROLE.REMPLACANT) {
                return true;
            }
        } else {
            return true;
        }

        return false;
    }

    private Utilisateur getUtilisateurByTarget(Object target) {
        if (!(target instanceof BasedObject)) {
            return null;
        }
        Long idUtilisateur = ((BasedObject) target).getIdUtilisateur();
        return utilisateurRepository.findById(idUtilisateur).orElse(null);
    }

    private boolean isSameClient(Utilisateur user, Long idUtilisateur) {
        Utilisateur targetUser = utilisateurRepository.findById(idUtilisateur).orElse(null);
        if (targetUser == null || targetUser.getClient() == null || user.getClient() == null) {
            return false;
        }
        return targetUser.getClient().getIdClient().equals(user.getClient().getIdClient());
    }

    private boolean isSameUser(Utilisateur user, Long idUtilisateur) {
        return idUtilisateur != null && user.getIdUtilisateur().equals(idUtilisateur);
    }
}
