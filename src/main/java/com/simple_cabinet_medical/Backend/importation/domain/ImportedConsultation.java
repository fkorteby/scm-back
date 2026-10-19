package com.simple_cabinet_medical.Backend.importation.domain;

import com.simple_cabinet_medical.Backend.model.Consultation;

import java.util.List;

public record ImportedConsultation(
        long oldNumCons,
        long oldNumMal,
        Long newPatientId,
        Consultation consultation,
        List<OldPrescription> prescriptions
) {
}