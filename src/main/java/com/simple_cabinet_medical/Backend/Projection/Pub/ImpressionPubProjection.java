package com.simple_cabinet_medical.Backend.Projection.Pub;

import com.simple_cabinet_medical.Backend.model.ImpressionPub;
import org.springframework.data.rest.core.config.Projection;

import java.time.LocalDateTime;

@Projection(name = "impressionPubInfo", types = ImpressionPub.class)
public interface ImpressionPubProjection {

    Long getIdImpressionPub();

    LocalDateTime getDateHeure();

    UtilisateurInfo getUtilisateur();
}
