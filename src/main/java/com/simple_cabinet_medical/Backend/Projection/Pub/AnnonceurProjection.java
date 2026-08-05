package com.simple_cabinet_medical.Backend.Projection.Pub;

import com.simple_cabinet_medical.Backend.model.Annonceur;
import com.simple_cabinet_medical.Backend.model.EStatusAnnonceur;
import com.simple_cabinet_medical.Backend.model.ETypeAnnonceur;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "annonceurProjection", types = Annonceur.class)
public interface AnnonceurProjection {

    Long getIdAnnonceur();

    String getNomAnnonceur();

    String getRaisonSociale();

    String getNumeroRegistreCommerce();

    String getNif();

    String getNis();

    String getRib();

    String getAdresseLegale();

    String getSecteurActivite();

    ETypeAnnonceur getTypeAnnonceur();

    EStatusAnnonceur getStatus();

    UtilisateurInfo getUtilisateur();
}
