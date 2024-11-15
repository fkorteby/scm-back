package com.simple_cabinet_medical.Backend.permisson;

import com.simple_cabinet_medical.Backend.model.Utilisateur;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class BasePermissionElevator implements PermissionEvaluator {

    Logger logger = LoggerFactory.getLogger(BasePermissionElevator.class);


    @Autowired
    MedecinPermissionImpl medecinPermission;

    @Autowired
    RemplacantPermissionImpl remplacantPermission;

    @Autowired
    SecretairePermissionImpl secretairePermission;

    @Override
    public boolean hasPermission(Authentication auth, Object targetObject, Object permission) {
        try {
            logger.debug("Check permission for" + " targetObject:" + targetObject.getClass() + " permission:"
                    + permission.toString() + " username:" + ((Utilisateur) auth.getPrincipal()).getNomUtilisateur());
            return hasAccess (targetObject, (User) auth.getPrincipal());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            return false;
        }
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        // I will not implement this method just because I don't need it in this demo.
        throw new UnsupportedOperationException();
    }

    public boolean hasAccess(Object targetObject, User utilisateur) {
        // Full access to ADMIN role
        if (utilisateur.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
            return true;
        }
        if (utilisateur.getAuthorities().contains(new SimpleGrantedAuthority("MEDECIN"))) {
            return medecinPermission.hasAccess(targetObject, utilisateur);
        }
        if (utilisateur.getAuthorities().contains(new SimpleGrantedAuthority("REMPLACENT"))) {

            return remplacantPermission.hasAccess(targetObject, utilisateur);
        }
        if (utilisateur.getAuthorities().contains(new SimpleGrantedAuthority("SECRETAIRE"))) {

            return secretairePermission.hasAccess(targetObject, utilisateur);
        }

        // else deny access
        return false;
    }
}
