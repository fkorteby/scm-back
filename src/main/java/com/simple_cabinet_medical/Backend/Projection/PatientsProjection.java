package com.simple_cabinet_medical.Backend.Projection;

import com.simple_cabinet_medical.Backend.model.Patient;
import org.springframework.data.rest.core.config.Projection;

import java.time.LocalDate;

@Projection(name = "patients", types = Patient.class)
public interface PatientsProjection {

    Long getIdPatient();

    String getNom();

    String getPrenom();

    LocalDate getDateNaissance();

    String getNumeroTel();

    String getSituation();

    Boolean getAssurance();

    String getAntecedentsPersonnelsMedicaux();

    String getAntecedentsPersonnelsChirugicaux();

    String getAntecedentsFamiliaux();

    String getAutres();
}
