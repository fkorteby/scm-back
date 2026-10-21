package com.simple_cabinet_medical.Backend.importation.domain;

import java.sql.Date;

public record OldPatient(
        long numMal,
        String nom,
        String prenom,
        Date ddn,
        String adresse,
        String tel,
        String sexe,
        String situation,
        String profession,
        String assurance,
        String antMed,
        String antChir,
        String antFam,
        String autres
) {
}
