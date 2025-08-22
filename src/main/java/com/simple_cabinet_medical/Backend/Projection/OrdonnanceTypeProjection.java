package com.simple_cabinet_medical.Backend.Projection;

import com.simple_cabinet_medical.Backend.model.OrdonnanceType;
import org.springframework.data.rest.core.config.Projection;

import java.util.Set;

@Projection(name = "ordananceTypeProjection", types = {OrdonnanceType.class})
public interface OrdonnanceTypeProjection {
    Long getIdOrdonnanceType();
    String getName();
    Set<TraitmentInfo> getTraitements();
}
