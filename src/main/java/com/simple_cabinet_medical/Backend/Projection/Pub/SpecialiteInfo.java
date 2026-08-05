package com.simple_cabinet_medical.Backend.Projection.Pub;

import com.simple_cabinet_medical.Backend.model.Specialite;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "specialiteInfo", types = Specialite.class)
public interface SpecialiteInfo {
    String getLibelle();
}
