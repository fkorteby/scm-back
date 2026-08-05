package com.simple_cabinet_medical.Backend.Projection;

import com.simple_cabinet_medical.Backend.model.Client;
import com.simple_cabinet_medical.Backend.model.ClientConfig;
import com.simple_cabinet_medical.Backend.model.EStatus;
import com.simple_cabinet_medical.Backend.model.EmailVerificationStatus;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "client", types = Client.class)
public interface ClientProjection {

    Long getIdClient();

    String getNomClient();

    String getNomClientEnArabe();

    String getEmail();

    String getContact();

    String getAdresse();
    String getVille();
    String getCodePostal();
    String getPays();
    String getRue();

    String getMobile();

    String getSite();

    String getDescription();

    String getImage();

    EStatus getStatus();

    String getSpecialite();

    EmailVerificationStatus getEmailVerificationStatus();

    ClientConfig getClientConfig();
}
