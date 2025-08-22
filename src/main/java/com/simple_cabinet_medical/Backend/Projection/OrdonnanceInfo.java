package com.simple_cabinet_medical.Backend.Projection;

import java.util.Set;

public interface OrdonnanceInfo {

    Long getIdOrdonnance();
    Set<TraitmentInfo> getTraitements();
}
