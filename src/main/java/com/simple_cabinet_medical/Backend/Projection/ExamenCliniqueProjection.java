package com.simple_cabinet_medical.Backend.Projection;

import com.simple_cabinet_medical.Backend.model.ExamenClinique;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "ExamenClinique", types = ExamenClinique.class)
public interface ExamenCliniqueProjection {

    Long getIdExamenClinique();
    Long getIdUtilisateur();
    String getNomExamenClinique();

    Long getIdParentExamenClinique();
}
