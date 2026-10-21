package com.simple_cabinet_medical.Backend.service.audit;

import com.simple_cabinet_medical.Backend.model.Utilisateur;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class CurrentUserProvider {

    /*
     * Rôles autorisés à consulter les logs de TOUS les clients (et donc à
     * filtrer par client). Les autres utilisateurs ne voient que les logs
     * de leur propre client.
     *
     * ⚠️ À ADAPTER : mets ici les noms réels de ton enum EROLE.
     */
    private static final Set<String> SUPER_ADMIN_ROLES = Set.of("ADMIN");

    public CurrentUser getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof Utilisateur utilisateur)) {
            return CurrentUser.anonymous();
        }

        Long userId = utilisateur.getIdUtilisateur();
        String username = utilisateur.getUsername(); // vient de UserDetails
        Long clientId = utilisateur.getClient() != null ? utilisateur.getClient().getIdClient() : null;
        String clientName = utilisateur.getClient() != null ? utilisateur.getClient().getNomClient() : null;

        boolean superAdmin = utilisateur.getRole() != null
                && SUPER_ADMIN_ROLES.contains(utilisateur.getRole().name());

        return new CurrentUser(userId, username, clientId, clientName, superAdmin);
    }

    public record CurrentUser(
            Long userId,
            String username,
            Long clientId,
            String clientName,
            boolean superAdmin
    ) {
        public static CurrentUser anonymous() {
            return new CurrentUser(null, "anonymous", null, null, false);
        }
    }
}