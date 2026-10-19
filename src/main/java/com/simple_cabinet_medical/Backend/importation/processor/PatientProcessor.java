package com.simple_cabinet_medical.Backend.importation.processor;

import com.simple_cabinet_medical.Backend.importation.domain.ImportedPatient;
import com.simple_cabinet_medical.Backend.importation.domain.OldPatient;
import com.simple_cabinet_medical.Backend.model.Patient;
import org.springframework.batch.item.ItemProcessor;

public class PatientProcessor
        implements ItemProcessor<OldPatient, ImportedPatient> {

    private final Long clientId;

    public PatientProcessor(Long clientId) {
        this.clientId = clientId;
    }

    @Override
    public ImportedPatient process(OldPatient oldPatient) {

        Patient patient = new Patient();

        patient.setNom(clean(oldPatient.nom()));
        patient.setPrenom(clean(oldPatient.prenom()));

        if (oldPatient.ddn() != null) {
            patient.setDateNaissance(oldPatient.ddn().toLocalDate());
        }

        patient.setNumeroTel(normalizePhone(oldPatient.tel()));
        patient.setSituation(clean(oldPatient.situation()));
        patient.setSexe(normalizeSexe(oldPatient.sexe()));
        patient.setAdresse(clean(oldPatient.adresse()));
        patient.setProfession(clean(oldPatient.profession()));
        patient.setAssurance(normalizeAssurance(oldPatient.assurance()));

        patient.setAntecedentsPersonnelsMedicaux(clean(oldPatient.antMed()));
        patient.setAntecedentsPersonnelsChirugicaux(clean(oldPatient.antChir()));
        patient.setAntecedentsFamiliaux(clean(oldPatient.antFam()));
        patient.setAutres(clean(oldPatient.autres()));

        // Fields not available in the old MDB
        patient.setCin(null);
        patient.setNumeroSecuriteSociale(null);
        patient.setPassport(null);
        patient.setActeNaissance(null);

        // Client information
        patient.setClientCreatorId(clientId);

        return new ImportedPatient(
                oldPatient.numMal(),
                patient
        );
    }

    private String clean(String value) {
        if (value == null) {
            return null;
        }

        String cleaned = value.trim();

        return cleaned.isEmpty() ? null : cleaned;
    }

    private String normalizePhone(String phone) {
        if (phone == null) {
            return null;
        }

        String normalized = phone.replaceAll("\\D", "");

        return normalized.isEmpty() ? null : normalized;
    }

    private String normalizeSexe(String sexe) {
        if (sexe == null) {
            return null;
        }

        String value = sexe.trim().toLowerCase();

        if (value.contains("f")) {
            return "Féminin";
        }

        if (value.contains("m")) {
            return "Masculin";
        }

        return sexe.trim();
    }

    private Boolean normalizeAssurance(String assurance) {
        if (assurance == null) {
            return false;
        }

        return assurance.trim().equalsIgnoreCase("Oui");
    }
}