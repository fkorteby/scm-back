package com.simple_cabinet_medical.Backend.Projection.Pub;

import com.simple_cabinet_medical.Backend.model.Wilaya;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "wilayaInfo", types = Wilaya.class)
public interface WilayaInfo {
    String getNomWilaya();
}
