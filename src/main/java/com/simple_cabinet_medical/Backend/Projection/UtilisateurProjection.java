package com.simple_cabinet_medical.Backend.Projection;

import com.simple_cabinet_medical.Backend.model.EROLE;
import com.simple_cabinet_medical.Backend.model.Utilisateur;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "utilisateur", types = Utilisateur.class)
public interface UtilisateurProjection {
    Long getIdUtilisateur();

    String getNom();

    String getPrenom();

    EROLE getRole();

    String getNomUtilisateur();

    ClientInfo getClient();
}
