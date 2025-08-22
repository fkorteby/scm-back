package com.simple_cabinet_medical.Backend.Projection;

import com.simple_cabinet_medical.Backend.model.Consultation;
import com.simple_cabinet_medical.Backend.model.EStatusConsultation;
import org.springframework.data.rest.core.config.Projection;

import java.time.LocalDate;
import java.util.Set;

@Projection(name = "consultations", types = Consultation.class)
public interface ConsultationsProjection {
    Long getIdConsultation();

    Long getIdUtilisateur();

    LocalDate getDateConsultation();

    EStatusConsultation getStatusConsultation();

    String getResultatExamenClinique();

    String getResultatExamenParacliniques();

    String getDiagnosticMedical();

    String getTraitement();

    PatientInfo getPatient();

    Set<TraitmentInfo> getTraitements();

    String getMotif();

    ConduiteInfo getConduite();
}