package com.simple_cabinet_medical.Backend.utils;

import com.simple_cabinet_medical.Backend.model.EROLE;
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
                logger.warn("Aucun utilisateur authentifié trouvé.");
                return Optional.empty();
            }

            Object principal = auth.getPrincipal();
            if (principal instanceof Utilisateur utilisateur) {

                // Cas ADMIN → clientCreatorId = 1
                if (utilisateur.getRole() == EROLE.ADMIN) {
                    logger.info("Utilisateur ADMIN détecté → clientCreatorId=1");
                    return Optional.of(1L);
                }

                // Cas non ADMIN → on doit avoir un client obligatoire
                if (utilisateur.getClient() == null || utilisateur.getClient().getIdClient() == null) {
                    logger.error("Utilisateur sans client associé → clientId obligatoire !");
                    throw new IllegalStateException("Un utilisateur non ADMIN doit avoir un client associé !");
                }

                return Optional.of(utilisateur.getClient().getIdClient());
            } else {
                logger.warn("Le principal authentifié n'est pas une instance de Utilisateur");
                return Optional.empty();
            }
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération de l'ID client", e);
            return Optional.empty();
        }
    }
}