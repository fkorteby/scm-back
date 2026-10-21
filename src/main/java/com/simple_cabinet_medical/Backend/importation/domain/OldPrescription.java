package com.simple_cabinet_medical.Backend.importation.domain;

public record OldPrescription(
        long numCons,
        long numMal,
        String medicPresc,
        String formPresc,
        String posoPresc,
        String dureePresc
) {
}