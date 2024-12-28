package com.simple_cabinet_medical.Backend.service;

import com.simple_cabinet_medical.Backend.repository.PatientRepository;
import org.springframework.stereotype.Service;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public void addPatient () {}

    public void deletePatient () {}

    public void updatePatient () {}

}
