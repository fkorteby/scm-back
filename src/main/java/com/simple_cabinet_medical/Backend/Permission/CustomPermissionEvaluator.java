package com.simple_cabinet_medical.Backend.Permission;

import com.simple_cabinet_medical.Backend.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {

    private static final Logger logger = LoggerFactory.getLogger(CustomPermissionEvaluator.class);
    private final EntityFetcher entityFetcher;

    public CustomPermissionEvaluator(EntityFetcher entityFetcher) {
        this.entityFetcher = entityFetcher;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication == null || targetDomainObject == null || permission == null) {
            logger.warn("Permission denied: null authentication, targetDomainObject, or permission");
            return false;
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof Utilisateur)) {
            logger.warn("Permission denied: principal is not a Utilisateur (actual type: {})",
                    principal != null ? principal.getClass().getSimpleName() : "null");
            return false;
        }

        Utilisateur currentUser = (Utilisateur) principal;
        if (isPrimitiveOrWrapper(targetDomainObject)) {
            return true;
        }
        boolean result = false;
        String perm = permission.toString().toUpperCase();

        switch (perm) {
            case "READ":
                result = checkReadPermission(currentUser, targetDomainObject, permission);
                break;

            case "WRITE":
                result = chekWritePermission(currentUser, targetDomainObject, permission);
                break;

            case "UPDATE":
                result = chekUpdatePermission(currentUser, targetDomainObject, permission);
                break;

            default:
                result = false;
        }
        return result;
    }


    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        logger.warn("Permission denied - targetType: {}, permission: {}, targetId: {}",
                targetType, permission, targetId);

        if (authentication == null || targetId == null || targetType == null || permission == null) {
            return false;
        }
        return checkDeletePermission((Utilisateur) authentication.getPrincipal(), targetType, targetId, permission);
    }

    private boolean chekWritePermission(Utilisateur currentUser, Object targetDomainObject, Object permission) {
        logger.info("=== Starting write permission check ===");
        logger.info("User: {}", currentUser != null ? currentUser.getUsername() : "null");
        logger.info("Role: {}", currentUser != null ? currentUser.getRole() : "null");
        logger.info("Target object: {}", targetDomainObject != null ? targetDomainObject.getClass().getSimpleName() : "null");
        logger.info("Requested permission: {}", permission);

        if (currentUser == null) {
            logger.warn("Permission denied: currentUser is null");
            return false;
        }

        if (targetDomainObject == null) {
            logger.warn("Permission denied: targetDomainObject is null");
            return false;
        }

        if (!"WRITE".equals(permission)) {
            logger.warn("Unsupported access level: {}", permission);
            return false;
        }

        EROLE role = currentUser.getRole();
        if (role == null) {
            logger.warn("Permission denied: User has no role assigned");
            return false;
        }

        if (!(role == EROLE.ADMIN || role == EROLE.MEDECIN_PRINCIPAL || role == EROLE.MEDECIN
                || role == EROLE.REMPLACANT || role == EROLE.SECRETAIRE)) {
            logger.warn("Permission denied: Unsupported role {}", role);
            return false;
        }

        if (role == EROLE.ADMIN) {
            logger.info("Permission granted: ADMIN has full write access");
            return true;
        }

        if (!(targetDomainObject instanceof BasedObject)) {
            logger.warn("Permission denied: Target object {} is not BasedObject", targetDomainObject.getClass().getSimpleName());
            return false;
        }

        if (role == EROLE.MEDECIN_PRINCIPAL || role == EROLE.MEDECIN) {
            if (targetDomainObject instanceof Client || targetDomainObject instanceof ClientConfig) {
                logger.info("Permission denied: {} cannot write Client or ClientConfig", role);
                return false;
            } else {
                logger.info("Permission granted: {} can write {}", role, targetDomainObject.getClass().getSimpleName());
                return true;
            }
        } else if (role == EROLE.REMPLACANT) {
            if (targetDomainObject instanceof Patient || targetDomainObject instanceof Consultation
                    || targetDomainObject instanceof Document || targetDomainObject instanceof RendezVous
                    || targetDomainObject instanceof Traitement) {
                logger.info("Permission granted: REMPLACANT can write {}", targetDomainObject.getClass().getSimpleName());
                return true;
            } else {
                logger.info("Permission denied: REMPLACANT cannot write {}", targetDomainObject.getClass().getSimpleName());
                return false;
            }
        } else if (role == EROLE.SECRETAIRE) {
            if (targetDomainObject instanceof Patient || targetDomainObject instanceof RendezVous) {
                logger.info("Permission granted: SECRETAIRE can write {}", targetDomainObject.getClass().getSimpleName());
                return true;
            } else {
                logger.info("Permission denied: SECRETAIRE cannot write {}", targetDomainObject.getClass().getSimpleName());
                return false;
            }
        }

        logger.warn("Permission denied: No matching rule for role {}", role);
        return false;
    }



    private boolean chekUpdatePermission(Utilisateur currentUser, Object targetDomainObject, Object permission) {
        if (!permission.equals("UPDATE")) {
            logger.warn("Unsupported access level: {}", permission);
            return false;
        }
        EROLE role = currentUser.getRole();
        if (!(role == EROLE.ADMIN || role == EROLE.MEDECIN_PRINCIPAL || role == EROLE.MEDECIN
                || role == EROLE.REMPLACANT || role == EROLE.SECRETAIRE)) {
            return false;
        }
        if (role == EROLE.ADMIN) return true;
        if (!(targetDomainObject instanceof BasedObject basedObject)) {
            logger.warn("Target object is not BasedObject: {}", targetDomainObject.getClass().getSimpleName());
            return false;
        }

        Long userClientId = null;
        Long userId = currentUser.getIdUtilisateur();

        if (currentUser.getRole() != null) {
            userClientId = currentUser.getClient().getIdClient();
        }

        Long clientObjectId = basedObject.getClientCreatorId();
        Long userObjectId = basedObject.getIdUtilisateur();

        if (role == EROLE.MEDECIN_PRINCIPAL) {
            if (targetDomainObject instanceof ClientConfig) {
                Client client = currentUser.getClient();
                Long clientIdConf = client.getClientConfig().getIdClientConfig();
                return clientIdConf.equals(((ClientConfig) targetDomainObject).getIdClientConfig());
            }
            if (targetDomainObject instanceof Client) {
                Long idClient = ((Client) targetDomainObject).getIdClient();
                return userClientId.equals(idClient);
            }
            return userClientId.equals(clientObjectId);
        } else if (role == EROLE.MEDECIN) {
            if (targetDomainObject instanceof Patient || targetDomainObject instanceof RendezVous) {
                return true;
            } else return userId.equals(userObjectId);
        } else if (role == EROLE.REMPLACANT) {
            if (targetDomainObject instanceof Patient || targetDomainObject instanceof RendezVous) {
                return true;
            } else if (targetDomainObject instanceof Consultation || targetDomainObject instanceof Document
                    || targetDomainObject instanceof Traitement) {
                return userId.equals(userObjectId);
            } else return false;
        } else if (role == EROLE.SECRETAIRE) {
            if (targetDomainObject instanceof Patient || targetDomainObject instanceof RendezVous) {
                return true;
            } else return false;
        }
        return false;
    }

    private boolean checkReadPermission(Utilisateur currentUser, Object targetDomainObject, Object permission) {
        if (!permission.equals("READ")) {
            logger.warn("Unsupported access level: {}", permission);
            return false;
        }
        EROLE role = currentUser.getRole();
        if (!(role == EROLE.ADMIN || role == EROLE.MEDECIN_PRINCIPAL || role == EROLE.MEDECIN
                        || role == EROLE.REMPLACANT || role == EROLE.SECRETAIRE)) {
            return false;
        }
        if (role == EROLE.ADMIN) return true;

        Long userClientId = null;

        if (currentUser.getRole() != null) {
            userClientId = currentUser.getClient().getIdClient();
        }
        if (!(targetDomainObject instanceof BasedObject basedObject)) {
            logger.warn("Target object is not BasedObject: {}", targetDomainObject.getClass().getSimpleName());
            return false;
        }

        Long clientObjectId = basedObject.getClientCreatorId();

        if (targetDomainObject instanceof ClientConfig){
            Long idClient = ((ClientConfig) targetDomainObject).getClient().getIdClient();
            return userClientId.equals(idClient);
        }
        if (role == EROLE.MEDECIN_PRINCIPAL || role == EROLE.MEDECIN) {
            if (targetDomainObject instanceof Client) {
                Long idClient = ((Client) targetDomainObject).getIdClient();
                return userClientId.equals(idClient);
            } else if (targetDomainObject instanceof Medicament || targetDomainObject instanceof Duree
                    || targetDomainObject instanceof Posologie || targetDomainObject instanceof Motif
                    || targetDomainObject instanceof Conduite || targetDomainObject instanceof Paraclinique
                    || targetDomainObject instanceof OptionParaclinique || targetDomainObject instanceof Forme
                    || targetDomainObject instanceof Diagnostic) {
                return userClientId.equals(clientObjectId) || clientObjectId == 1;
            } else
                return userClientId.equals(clientObjectId);
        }
        if (role == EROLE.REMPLACANT || role == EROLE.SECRETAIRE) {
            if (targetDomainObject instanceof Medicament || targetDomainObject instanceof Duree
                    || targetDomainObject instanceof Posologie || targetDomainObject instanceof Motif
                    || targetDomainObject instanceof Conduite || targetDomainObject instanceof Paraclinique
                    || targetDomainObject instanceof OptionParaclinique || targetDomainObject instanceof Forme
                    || targetDomainObject instanceof Diagnostic || targetDomainObject instanceof ClientConfig ) {
                return userClientId.equals(clientObjectId) || clientObjectId == 1;
            } else if (targetDomainObject instanceof Local || targetDomainObject instanceof Client) {
                return false;
            } else return userClientId.equals(clientObjectId);
        }
        return false;
    }

    private boolean checkDeletePermission(Utilisateur currentUser, String targetType, Serializable targetId, Object permission) {
        if (!permission.equals("DELETE")) {
            logger.warn("Unsupported access level: {}", permission);
            return false;
        }
        EROLE role = currentUser.getRole();
        if (role == null) return false;
        if (role == EROLE.ADMIN) return true;

        Long userClientId = null;
        Long userId = currentUser.getIdUtilisateur();

        if (currentUser.getRole() != null) {
            userClientId = currentUser.getClient().getIdClient();
        }
        Long entityId = (Long) targetId;
        BasedObject targetDomainObject = this.entityFetcher.getEntity(targetType, entityId);
        logger.warn("Entity: {}", targetDomainObject);

        Long clientObjectId = targetDomainObject.getClientCreatorId();
        Long userObjectId = targetDomainObject.getIdUtilisateur();

        if (role == EROLE.MEDECIN_PRINCIPAL) {
            return userClientId.equals(clientObjectId);
        }
        // Règles selon le type
        if (targetDomainObject instanceof Patient || targetDomainObject instanceof RendezVous) {
            return userClientId.equals(clientObjectId);
        }
        if (targetDomainObject instanceof Consultation || targetDomainObject instanceof Document
                || targetDomainObject instanceof Traitement || targetDomainObject instanceof OrdonnanceType
                || targetDomainObject instanceof LettreOrientationType || targetDomainObject instanceof CertificatType
                || targetDomainObject instanceof ExamenParacliniqueType) {
            return userId.equals(userObjectId)
                    && (role.equals(EROLE.MEDECIN) || role.equals(EROLE.REMPLACANT));
        } else if (targetDomainObject instanceof Medicament || targetDomainObject instanceof Duree
                || targetDomainObject instanceof Posologie || targetDomainObject instanceof Motif
                || targetDomainObject instanceof Conduite || targetDomainObject instanceof Paraclinique
                || targetDomainObject instanceof OptionParaclinique || targetDomainObject instanceof Forme
                || targetDomainObject instanceof Diagnostic) {
            return userId.equals(userObjectId)
                    && (role.equals(EROLE.MEDECIN));
        }
        return false;
    }

    private boolean isPrimitiveOrWrapper(Object obj) {
        return obj instanceof Number ||
                obj instanceof Boolean ||
                obj instanceof Character ||
                obj instanceof String ||
                !isInstanceOfBaseObject(obj) ||
                obj.getClass().isPrimitive();
    }

    private boolean isInstanceOfBaseObject(Object object) {
        if (object instanceof BasedObject) {
            return true;
        } else return false;
    }
}
