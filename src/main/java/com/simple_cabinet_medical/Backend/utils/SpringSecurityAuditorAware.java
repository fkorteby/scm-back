package com.simple_cabinet_medical.Backend.utils;

import com.simple_cabinet_medical.Backend.model.Utilisateur;
import com.simple_cabinet_medical.Backend.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SpringSecurityAuditorAware  implements AuditorAware<Long> {

    @Autowired
    UtilisateurRepository utilisateurRepository;

    @Override
    public Optional<Long> getCurrentAuditor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return Optional.empty();
        }
        Utilisateur userDetails = (Utilisateur) auth.getPrincipal();
        String username = userDetails.getUsername();
        Optional<Utilisateur> user = utilisateurRepository.findByNomUtilisateur(username);
        return Optional.ofNullable(user.get().getIdUtilisateur());
    }
}
