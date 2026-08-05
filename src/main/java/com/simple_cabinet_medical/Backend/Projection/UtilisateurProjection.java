package com.simple_cabinet_medical.Backend.Projection;

import com.simple_cabinet_medical.Backend.model.EROLE;
import com.simple_cabinet_medical.Backend.model.EStatus;
import com.simple_cabinet_medical.Backend.model.Utilisateur;
import org.springframework.data.rest.core.config.Projection;

import javax.lang.model.util.ElementScanner6;

@Projection(name = "utilisateur", types = Utilisateur.class)
public interface UtilisateurProjection {
    Long getIdUtilisateur();

    String getNom();

    String getPrenom();

    EROLE getRole();

    EStatus getStatus();

    String getNomUtilisateur();

    ClientInfo getClient();
}
