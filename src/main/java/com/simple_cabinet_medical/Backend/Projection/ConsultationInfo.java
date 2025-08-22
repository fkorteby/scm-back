package com.simple_cabinet_medical.Backend.Projection;

import java.time.LocalDate;

public interface ConsultationInfo {
    LocalDate getDateConsultation();

    String getDiagnosticMedical();

    PatientInfo getPatient();

    String getMotif();
}
