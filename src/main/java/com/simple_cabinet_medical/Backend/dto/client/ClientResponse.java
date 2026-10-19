package com.simple_cabinet_medical.Backend.dto.client;

import com.simple_cabinet_medical.Backend.model.EStatus;
import com.simple_cabinet_medical.Backend.model.EmailVerificationStatus;

public record ClientResponse(
        Long idClient,
        String nomClient,
        String nomClientEnArabe,
        String email,
        String contact,
        String adresse,
        String ville,
        String codePostal,
        String pays,
        String rue,
        String mobile,
        String site,
        String description,
        String image,
        EStatus status,
        String specialite,
        EmailVerificationStatus emailVerificationStatus
) {
}