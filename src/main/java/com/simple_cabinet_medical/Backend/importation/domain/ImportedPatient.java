package com.simple_cabinet_medical.Backend.importation.domain;

import com.simple_cabinet_medical.Backend.model.Patient;

public record ImportedPatient(
        long oldNumMal,
        Patient patient
) {
}
