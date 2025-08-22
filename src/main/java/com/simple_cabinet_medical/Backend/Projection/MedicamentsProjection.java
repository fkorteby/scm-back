package com.simple_cabinet_medical.Backend.Projection;

import com.simple_cabinet_medical.Backend.model.Forme;
import com.simple_cabinet_medical.Backend.model.Medicament;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "medicamentsProjection", types = {Medicament.class})
public interface MedicamentsProjection {
    Long getIdMedicament();

    String getNomCommerciale();

    String getDci();

    String getDosage();

    String getConditionnement();

    Forme getForme();
}
