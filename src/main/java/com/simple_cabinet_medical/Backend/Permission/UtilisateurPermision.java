package com.simple_cabinet_medical.Backend.Permission;

import com.simple_cabinet_medical.Backend.Dto.DiagnosticDTO;
import com.simple_cabinet_medical.Backend.model.EROLE;
import com.simple_cabinet_medical.Backend.model.Utilisateur;
import com.simple_cabinet_medical.Backend.repository.UtilisateurRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("authz")
public class UtilisateurPermision {
    private static final Logger logger = LoggerFactory.getLogger(CustomPermissionEvaluator.class);

    private final UtilisateurRepository utilisateurRepository;

    public UtilisateurPermision(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    public boolean canChangeClientProprite(Long id) {
        Utilisateur currentUser = getCurrentUser();
        if (currentUser == null) {
            return false;
        }
        EROLE role = currentUser.getRole();
        if (role == null) {
            return false;
        }
        if (role == EROLE.ADMIN) {
            return true;
        }
        if (role == EROLE.MEDECIN_PRINCIPAL) {
            Long userClientId = currentUser.getClient().getIdClient();
            return userClientId.equals(id);
        }
        return false;
    }

    public boolean hasReadCustomPermission(Object targetDomainObject) {
        logger.warn("Permission denied: target is null for user ruuuuun");
        if (targetDomainObject == null) {
            logger.warn("Permission denied: target is null");
            return false;
        }
        if (!(targetDomainObject instanceof Utilisateur utilisateur)) {
            logger.warn("Target object is not Utilisateur: {}", targetDomainObject.getClass().getSimpleName());
            return false;
        }
        Utilisateur currentUser = getCurrentUser();
        EROLE role = currentUser.getRole();
        Long userClientId = null;
        Long userId = currentUser.getIdUtilisateur();

        if (role == null) return false;
        if (userId.equals(utilisateur.getIdUtilisateur())){
            return true ;
        }
        if (role == EROLE.ADMIN) return true;
        if (currentUser.getRole() != EROLE.ADMIN) {
            userClientId = currentUser.getClient().getIdClient();
        }
        Long clientObjectId = utilisateur.getClient().getIdClient();
        if (clientObjectId == userClientId && utilisateur.getRole() == EROLE.MEDECIN_PRINCIPAL) {
            return true;
        }
        return false;
    }

    public boolean hasDeleteCustomPermission(Long id) {

        if (id==null) return false;
        Utilisateur currentUser = getCurrentUser();
        Long userId = currentUser.getIdUtilisateur();

        if (userId.equals(id)){
            return false;
        }

        EROLE role = currentUser.getRole();
        Long userClientId = null;
        if (role == null) return false;
        if (role == EROLE.ADMIN) return true;

        if (currentUser.getRole() != EROLE.ADMIN) {
            userClientId = currentUser.getClient().getIdClient();
        }

        Utilisateur entityUser = getEntityUser(id);

        if (role == EROLE.MEDECIN_PRINCIPAL) {
            return userClientId.equals(entityUser.getClient().getIdClient());
        }
        return false;
    }
    public boolean hasUpdateCustomPermission(Long id) {

        if (id==null) return false;
        Utilisateur currentUser = getCurrentUser();
        Long userId = currentUser.getIdUtilisateur();

        if (userId.equals(id)){
            return true;
        }

        EROLE role = currentUser.getRole();
        Long userClientId = null;
        if (role == null) return false;
        if (role == EROLE.ADMIN) return true;

        if (currentUser.getRole() != EROLE.ADMIN) {
            userClientId = currentUser.getClient().getIdClient();
        }

        Utilisateur entityUser = getEntityUser(id);

        if (role == EROLE.MEDECIN_PRINCIPAL) {
            return userClientId.equals(entityUser.getClient().getIdClient());
        }
        return false;
    }
    public boolean hasChangePasswordCustomPermission(Long id) {

        if (id==null) return false;
        Utilisateur currentUser = getCurrentUser();
        Long userId = currentUser.getIdUtilisateur();

        if (userId.equals(id)){
            return true;
        }

        EROLE role = currentUser.getRole();
        if (role == null) return false;
        if (role == EROLE.ADMIN) return true;

        return false;
    }

    public Utilisateur getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        Object principal = authentication.getPrincipal();

        if (principal instanceof Utilisateur utilisateur) {
            return utilisateur;
        }
        return null;
    }
    public Utilisateur getEntityUser(Long id) {
       return utilisateurRepository.findById(id).orElse(null);
    }

}
