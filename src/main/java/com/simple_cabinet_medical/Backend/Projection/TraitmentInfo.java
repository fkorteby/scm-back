package com.simple_cabinet_medical.Backend.Projection;

import com.simple_cabinet_medical.Backend.model.Medicament;

public interface TraitmentInfo {
    Long getIdTraitement();

    Long getIdUtilisateur();

    String getPosologie();

    String getDuree();

    Medicament getMedicament();

}
