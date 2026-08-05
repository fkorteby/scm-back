package com.simple_cabinet_medical.Backend.Projection.Pub;

import com.simple_cabinet_medical.Backend.model.CompagnePublicitaire;
import com.simple_cabinet_medical.Backend.model.EFormatAffichePub;
import com.simple_cabinet_medical.Backend.model.EStatusCompagnePub;
import com.simple_cabinet_medical.Backend.model.ETypeAnnonce;
import org.springframework.data.rest.core.config.Projection;

import java.time.LocalDate;

@Projection(name = "compagnePubProjection", types = CompagnePublicitaire.class)
public interface CompagnePublicitaireProjection {

    Long getIdCompagnePub();
    String getNomCompagnePub();
    Long getBudjet();
    String getDescription();
    String getLien();
    EStatusCompagnePub getStatus();
    ETypeAnnonce getType();
    EFormatAffichePub getFormatAffichePub();
    LocalDate getDateDebut();
    LocalDate getDateFin();
    String getWilayaCible();
    String getSpecialiteCible();
}
