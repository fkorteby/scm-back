package com.simple_cabinet_medical.Backend.Projection;

import com.simple_cabinet_medical.Backend.model.Duree;
import com.simple_cabinet_medical.Backend.model.Medicament;
import com.simple_cabinet_medical.Backend.model.Posologie;

public interface TraitmentInfo {
    Long getIdTraitement();

    Posologie getPosologie();

    Duree getDuree();

    Medicament getMedicament();

}
