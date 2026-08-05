package com.simple_cabinet_medical.Backend.Projection;

import java.time.LocalDate;

public interface ConsultationInfo {
    LocalDate getDateConsultation();

    Long getIdUtilisateur();
    String getDiagnosticMedical();

    PatientInfo getPatient();

    String getMotifConsultation();

    String getCatEvolution();
}
