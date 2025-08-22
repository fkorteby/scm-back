package com.simple_cabinet_medical.Backend.service;

import com.simple_cabinet_medical.Backend.Dto.PatientResponseDto;
import com.simple_cabinet_medical.Backend.Mapper.Patient.PatientMapperImp;
import com.simple_cabinet_medical.Backend.model.Patient;
import com.simple_cabinet_medical.Backend.repository.PatientRepository;
import com.simple_cabinet_medical.Backend.repository.RendezVousRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class PatientService {
    private final PatientRepository patientRepository;
    private final RendezVousRepository rendezVousRepository;

    public PatientService(PatientRepository patientRepository, RendezVousRepository rendezVousRepository) {
        this.patientRepository = patientRepository;
        this.rendezVousRepository = rendezVousRepository;
    }
    public Page<PatientResponseDto> findPatientsWithRendezVousToday(Long idClient, Date date, int page, int size) {
        Page<Patient> patients=patientRepository
                .findPatientByClientAndRendezVousAujourdhui
                        (idClient ,date, PageRequest.of(page, size));
        return patients.map(new PatientMapperImp()::DtoFromEntity);
    }
}
