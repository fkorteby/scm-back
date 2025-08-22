//package com.simple_cabinet_medical.Backend.Permission;
//
//import com.simple_cabinet_medical.Backend.model.*;
//import com.simple_cabinet_medical.Backend.repository.UtilisateurRepository;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.security.access.PermissionEvaluator;
//import org.springframework.security.core.Authentication;
//import org.springframework.stereotype.Component;
//
//import java.io.Serializable;
//import java.util.Optional;
//
//import static com.simple_cabinet_medical.Backend.model.EAccessLevel.*;
//
//@Component
//public class CustomPermissionEvaluatorrr implements PermissionEvaluator {
//
//    private static final Logger logger = LoggerFactory.getLogger(CustomPermissionEvaluatorrr.class);
//
//    private final UtilisateurRepository utilisateurRepository;
//    private final EntityFetcher entityFetcher;
//
//    public CustomPermissionEvaluatorrr(UtilisateurRepository utilisateurRepository, EntityFetcher entityFetcher) {
//        this.utilisateurRepository = utilisateurRepository;
//        this.entityFetcher = entityFetcher;
//    }
//
//    @Override
//    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
//        System.out.println("Permission check: " + targetDomainObject + ", permission: " + permission);
//        logger.info("Checking permission: {} on object: {} for user authentication", permission,
//                targetDomainObject != null ? targetDomainObject.getClass().getSimpleName() : "null");
//        String finalClassName;
//        Object finalObject;
//        if (authentication == null || targetDomainObject == null || permission == null) {
//            logger.warn("Permission denied: null authentication, target, or permission");
//            return false;
//        }
//        if (targetDomainObject instanceof Optional<?> optional && optional.isPresent()) {
//            targetDomainObject = optional.get();
//        }
//
////        else if(targetDomainObject instanceof Page) {
////            // Check just the first element
////            if (((Page<?>)targetDomainObject).getContent() != null && ((Page<?>)targetDomainObject).getContent().size() >0) {
////                finalClassName = ((Page<?>)targetDomainObject).getContent().get(0).getClass().getSimpleName();
////                finalObject = ((Page) targetDomainObject).getContent().get(0);
////                logger.info("Target domain object class pageee: {}", finalClassName);
////            } else {
////                // page is empty return true
////                return true;
////            }
////        }
//
//        // For Page typ
//        Utilisateur currentUser = (Utilisateur) authentication.getPrincipal();
//        EROLE role = currentUser.getRole();
//        String className = targetDomainObject.getClass().getSimpleName();
//        logger.info("Target domain object class: {}", className);
//        logger.info("User ID: {}, Role: {}", currentUser.getIdUtilisateur(), role);
//
//        if (role == null) {
//            logger.warn("Permission denied: null role for user {}", currentUser.getIdUtilisateur());
//            return false;
//        }
//
//
//        boolean result = false;
//
//        switch (role) {
//            case ADMIN:
//                logger.info("User is ADMIN, granting permission");
//                result = true;
//                break;
//
//            case MEDECIN:
//                result = handleMedecinPermissions(currentUser, targetDomainObject, permission);
//                logger.info("MEDECIN permission result: {} for permission {}", result, permission);
//                break;
//
//            case REMPLACANT:
//                result = handleRemplacantPermissions(currentUser, targetDomainObject, permission);
//                logger.info("REMPLACANT permission result: {}", result);
//                break;
//
//            case SECRETAIRE:
//                result = handleSecretairePermissions(currentUser, targetDomainObject, permission);
//                logger.info("SECRETAIRE permission result: {}", result);
//                break;
//
//            default:
//                logger.warn("Unknown role: {}", role);
//                result = false;
//        }
//
//        logger.info("Final permission result: {} for user {} with role {}", result, currentUser.getIdUtilisateur(), role);
//        return result;
//    }
//
//    @Override
//    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object
//            permission) {
//        logger.info("Checking permission by ID: {} on type: {} with ID: {} for user authentication",
//                permission, targetType, targetId);
//
//        if (authentication == null || targetId == null || targetType == null || permission == null) {
//            logger.warn("Permission denied: null authentication, targetId, targetType, or permission");
//            return false;
//        }
//
//        Utilisateur currentUser = (Utilisateur) authentication.getPrincipal();
//        EROLE role = currentUser.getRole();
//
//        logger.info("User ID: {}, Role: {}", currentUser.getIdUtilisateur(), role);
//
//        Object targetDomainObject = entityFetcher.getEntity(targetType, (Long) targetId);
//        logger.info("Retrieved entity: {}", targetDomainObject != null ? targetDomainObject.getClass().getSimpleName() : "null");
//
//        if (role == null) {
//            logger.warn("Permission denied: null role for user {}", currentUser.getIdUtilisateur());
//            return false;
//        }
//
//        boolean result = false;
//
//        switch (role) {
//            case ADMIN:
//                logger.info("User is ADMIN, granting permission");
//                result = true;
//                break;
//
//            case MEDECIN:
//                result = handleMedecinPermissions(currentUser, targetDomainObject, permission);
//                logger.info("MEDECIN permission result: {}", result);
//                break;
//
//            case REMPLACANT:
//                result = handleRemplacantPermissions(currentUser, targetDomainObject, permission);
//                logger.info("REMPLACANT permission result: {}", result);
//                break;
//
//            case SECRETAIRE:
//                result = handleSecretairePermissions(currentUser, targetDomainObject, permission);
//                logger.info("SECRETAIRE permission result: {}", result);
//                break;
//
//            default:
//                logger.warn("Unknown role: {}", role);
//                result = false;
//        }
//
//        logger.info("Final permission result: {} for user {} with role {}", result, currentUser.getIdUtilisateur(), role);
//        return result;
//    }
//
//    private boolean handleMedecinPermissions(Utilisateur user, Object target, Object permission) {
//        logger.info("Handling MEDECIN permissions for user: {}, target: {}, permission: {}",
//                user.getIdUtilisateur(),
//                target != null ? target.getClass().getSimpleName() : "null",
//                permission);
//
//        boolean result = false;
//        if (permission.equals(READ.toString())) {
//            logger.info("Processing READ permission for MEDECIN");
//            result = handleReadAccess(user, target);
//            logger.info("READ access result: {}", result);
//        } else if (permission.equals(WRITE.toString()) || permission.equals(UPDATE.toString()) || permission.equals(DELETE.toString())) {
//            logger.info("Processing WRITE/UPDATE/DELETE permission for MEDECIN");
//            result = handleWriteAccessForMedecin(user, target);
//            logger.info("WRITE access result: {}", result);
//        }
//        return result;
//    }
//
//    private boolean handleRemplacantPermissions(Utilisateur user, Object target, Object permission) {
//        logger.info("Handling REMPLACANT permissions for user: {}, target: {}, permission: {}",
//                user.getIdUtilisateur(),
//                target != null ? target.getClass().getSimpleName() : "null",
//                permission);
//
//        boolean result = false;
//        if (permission.equals(READ.toString())) {
//            logger.info("Processing READ permission for REMPLACANT");
//            result = handleReadAccess(user, target);
//            logger.info("READ access result: {}", result);
//        } else if (permission.equals(DELETE.toString()) || permission.equals(UPDATE.toString())) {
//            if (target instanceof Patient) {
//                logger.info("Target is a Patient, granting permission");
//                return true;
//            }
//            Utilisateur targetUser = getUtilisateurByTarget(target);
//            logger.info("Target user: {}", targetUser != null ? targetUser.getIdUtilisateur() : "null");
//            result = isSameUser(user, targetUser != null ? targetUser.getIdUtilisateur() : null);
//            logger.info("isSameUser result: {}", result);
//        }
//        return result;
//    }
//
//    private boolean handleSecretairePermissions(Utilisateur user, Object target, Object permission) {
//        logger.info("Handling SECRETAIRE permissions for user: {}, target: {}, permission: {}",
//                user.getIdUtilisateur(),
//                target != null ? target.getClass().getSimpleName() : "null",
//                permission);
//
//        boolean result = false;
//        if (permission.equals(READ.toString())) {
//            Utilisateur targetUser = getUtilisateurByTarget(target);
//            logger.info("Target user for READ: {}", targetUser != null ? targetUser.getIdUtilisateur() : "null");
//            result = isSameClient(user, targetUser != null ? targetUser.getIdUtilisateur() : null);
//            logger.info("isSameClient result: {}", result);
//        } else if (permission.equals(WRITE.toString()) || permission.equals(DELETE.toString()) || permission.equals(UPDATE.toString())) {
//            if (target instanceof Patient) {
//                logger.info("Target is a Patient, granting permission");
//                return true;
//            }
//        }
//        return result;
//    }
//
//    private boolean handleReadAccess(Utilisateur user, Object target) {
//        logger.info("Handling READ access for user: {}, target: {}",
//                user.getIdUtilisateur(),
//                target != null ? target.getClass().getSimpleName() : "null");
//
//        if (target instanceof Client) {
//            Client client = (Client) target;
//            return user.getClient().getIdClient().equals(client.getIdClient());
//        } else if (target instanceof Consultation) {
//            Consultation consultation = (Consultation) target;
//            return consultation.getClient() != null && user.getClient().getIdClient().equals(consultation.getClient().getIdClient());
////        } else if (target instanceof Ordonnance) {
////            Ordonnance ordonnance = (Ordonnance) target;
////            Consultation consultation = ordonnance.getConsultation();
////            return consultation != null &&
////                    user.getClient().getIdClient().equals(consultation.getClient().getIdClient());
//        } else if (target instanceof Patient) {
//            Patient patient = (Patient) target;
//            return patient.getClient() != null && user.getClient().getIdClient().equals(patient.getClient().getIdClient());
//        }else if (target instanceof BasedObject) {
//            return isSameClient(user, ((BasedObject) target).getIdUtilisateur());
//        }
//        return false;
//    }
//
//    private boolean handleWriteAccessForMedecin(Utilisateur user, Object target) {
//        logger.info("Handling WRITE access for MEDECIN, user: {}, target: {}",
//                user.getIdUtilisateur(),
//                target != null ? target.getClass().getSimpleName() : "null");
//
//        if (target instanceof Consultation
//                || target instanceof Traitement || target instanceof Document) {
//
//            Utilisateur targetUser = getUtilisateurByTarget(target);
//            logger.info("Target user: {}", targetUser != null ? targetUser.getIdUtilisateur() : "null");
//
//            if (targetUser == null) {
//                logger.warn("Target user is null");
//                return false;
//            }
//
//        if (targetUser.getRole() == EROLE.MEDECIN) {
//            boolean result = isSameUser(user, targetUser.getIdUtilisateur());
//            logger.info("Target is MEDECIN, isSameUser result: {}", result);
//            return result;
//        } else if (targetUser.getRole() == EROLE.REMPLACANT) {
//            logger.info("Target is REMPLACANT, granting permission");
//            return true;
//        } else {
//            logger.info("Target has role {}, denying permission", targetUser.getRole());
//        }
//    } else {
//            logger.info("Target is not Ordonnance/Consultation/Traitement/Document, granting permission");
//            return true;
//        }
//
//        return false;
//    }
//
//    private Utilisateur getUtilisateurByTarget(Object target) {
//        if (!(target instanceof BasedObject)) {
//            logger.warn("Target is not a BasedObject");
//            return null;
//        }
//
//        Long idUtilisateur = ((BasedObject) target).getIdUtilisateur();
//        logger.info("Getting user by target, ID: {}", idUtilisateur);
//
//        Utilisateur user = utilisateurRepository.findById(idUtilisateur).orElse(null);
//        logger.info("Retrieved user: {}", user != null ? user.getIdUtilisateur() : "null");
//
//        return user;
//    }
//
//    private boolean isSameClient(Utilisateur user, Long idUtilisateur) {
//        logger.info("Checking if same client: user ID {}, target ID {}", user.getIdUtilisateur(), idUtilisateur);
//
//        Utilisateur targetUser = utilisateurRepository.findById(idUtilisateur).orElse(null);
//
//        if (targetUser == null) {
//            logger.warn("Target user not found");
//            return false;
//        }
//
//        if (targetUser.getClient() == null) {
//            logger.warn("Target user has no client");
//            return false;
//        }
//
//        if (user.getClient() == null) {
//            logger.warn("Current user has no client");
//            return false;
//        }
//
//        boolean result = targetUser.getClient().getIdClient().equals(user.getClient().getIdClient());
//        logger.info("isSameClient result: {} (current client: {}, target client: {})",
//                result,
//                user.getClient().getIdClient(),
//                targetUser.getClient().getIdClient());
//
//        return result;
//    }
//
//    private boolean isSameUser(Utilisateur user, Long idUtilisateur) {
//        boolean result = idUtilisateur != null && user.getIdUtilisateur().equals(idUtilisateur);
//        logger.info("isSameUser result: {} (current: {}, target: {})", result, user.getIdUtilisateur(), idUtilisateur);
//        return result;
//    }
//}