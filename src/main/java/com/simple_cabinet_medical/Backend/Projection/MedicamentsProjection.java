package com.simple_cabinet_medical.Backend.Projection;

import com.simple_cabinet_medical.Backend.model.Medicament;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "medicamentsProjection", types = {Medicament.class})
public interface MedicamentsProjection {
    Long getIdMedicament();

    Long getClientCreatorId();

    String getNomCommerciale();

    Long getIdUtilisateur();

    String getDci();

    String getDosage();

    String getConditionnement();

    String getForme();

    String getLaboMedicament();

    String getRemMedicament();

    String getDuree();

    String getPosologie();

}
