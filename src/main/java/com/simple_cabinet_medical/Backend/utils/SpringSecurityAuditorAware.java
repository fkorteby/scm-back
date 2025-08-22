package com.simple_cabinet_medical.Backend.utils;

import com.simple_cabinet_medical.Backend.model.Utilisateur;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SpringSecurityAuditorAware implements AuditorAware<Long> {

    private static final Logger logger = LoggerFactory.getLogger(SpringSecurityAuditorAware.class);

    @Override
    public Optional<Long> getCurrentAuditor() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
                logger.warn("No authenticated user found.");
                return Optional.empty();
            }

            Object principal = auth.getPrincipal();
            if (principal instanceof Utilisateur utilisateur) {
                return Optional.ofNullable(utilisateur.getIdUtilisateur());
            } else {
                logger.warn("Authenticated principal is not instance of Utilisateur");
                return Optional.empty();
            }
        } catch (Exception e) {
            logger.error("Error while retrieving the current auditor", e);
            return Optional.empty();
        }
    }

    public Optional<Long> getCurrentClientId() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
                logger.warn("No authenticated user found.");
                return Optional.empty();
            }

            Object principal = auth.getPrincipal();
            if (principal instanceof Utilisateur utilisateur) {
                // Supposons que Utilisateur a une méthode getIdClient()
                return Optional.ofNullable(utilisateur.getClient().getIdClient());
            } else {
                logger.warn("Authenticated principal is not instance of Utilisateur");
                return Optional.empty();
            }
        } catch (Exception e) {
            logger.error("Error while retrieving the current client ID", e);
            return Optional.empty();
        }
    }
}