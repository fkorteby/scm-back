package com.simple_cabinet_medical.Backend.service;

import com.simple_cabinet_medical.Backend.Dto.*;
import com.simple_cabinet_medical.Backend.Mapper.Patient.PatientMapperImp;
import com.simple_cabinet_medical.Backend.model.Patient;
import com.simple_cabinet_medical.Backend.repository.ConsultationRepository;
import com.simple_cabinet_medical.Backend.repository.DocumentRepository;
import com.simple_cabinet_medical.Backend.repository.PatientRepository;
import com.simple_cabinet_medical.Backend.repository.RendezVousRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Service
public class PatientService {
    private final PatientRepository patientRepository;
    private final ConsultationRepository consultationRepository;
    private final DocumentRepository documentRepository;
    private final RendezVousRepository rendezVousRepository;
    private static final Logger logger = LoggerFactory.getLogger(PatientService.class);

    public PatientService(PatientRepository patientRepository, ConsultationRepository consultationRepository, DocumentRepository documentRepository, RendezVousRepository rendezVousRepository) {
        this.patientRepository = patientRepository;
        this.consultationRepository = consultationRepository;
        this.documentRepository = documentRepository;
        this.rendezVousRepository = rendezVousRepository;
    }

    public Page<PatientResponseDto> findPatientsWithRendezVousToday(Long idClient, Date date, int page, int size) {
        Page<Patient> patients = patientRepository
                .findPatientByClientAndRendezVousAujourdhui
                        (idClient, date, PageRequest.of(page, size));
        return patients.map(new PatientMapperImp()::DtoFromEntity);
    }

    public Page<PatientsRapportDTO> getRapportByClient(Long clientId, String dateDebut, String dateFin,
                                                       String sexe, String situation, String assurance,
                                                       Integer ageMin, Integer ageMax, Pageable pageable) {
        Page<Patient> patients = patientRepository.findRapportByClient(
                clientId, dateDebut, dateFin, sexe, situation, assurance, ageMin, ageMax, pageable);
        return patients.map(this::mapToDTO);
    }

    public Page<PatientsRapportDTO> getRapportAll(String dateDebut, String dateFin, String sexe,
                                                  String situation, String assurance, Integer ageMin,
                                                  Integer ageMax, Pageable pageable) {
        Page<Patient> patients = patientRepository.findRapportAll(
                dateDebut, dateFin, sexe, situation, assurance, ageMin, ageMax, pageable);
        return patients.map(this::mapToDTO);
    }

    // export


    public List<PatientsRapportDTO> getRapportByClientForExport(Long clientId, String dateDebut, String dateFin,
                                                                String sexe, String situation, String assurance,
                                                                Integer ageMin, Integer ageMax) {
        List<Patient> patients = patientRepository.findRapportForExportByClient(
                clientId, dateDebut, dateFin, sexe, situation, assurance, ageMin, ageMax);
        return patients.stream()
                .map(this::mapToDTO)
                .toList();
    }

    public List<PatientsRapportDTO> getRapportAllForExport(String dateDebut, String dateFin, String sexe,
                                                           String situation, String assurance, Integer ageMin,
                                                           Integer ageMax) {
        List<Patient> patients = patientRepository.findRapportAllForExport(
                dateDebut, dateFin, sexe, situation, assurance, ageMin, ageMax);
        return patients.stream()
                .map(this::mapToDTO)
                .toList();
    }

    private PatientsRapportDTO mapToDTO(Patient patient) {
        PatientsRapportDTO dto = new PatientsRapportDTO();
        dto.setIdPatient(patient.getIdPatient());
        dto.setNom(patient.getNom());
        dto.setPrenom(patient.getPrenom());
        dto.setDateNaissance(patient.getDateNaissance());
        dto.setNumeroTel(patient.getNumeroTel());
        dto.setSituation(patient.getSituation());
        dto.setAssurance(patient.getAssurance());
        dto.setCin(patient.getCin());
        dto.setSexe(patient.getSexe());
        dto.setNumeroSecuriteSociale(patient.getNumeroSecuriteSociale());
        dto.setPassport(patient.getPassport());
        dto.setActeNaissance(patient.getActeNaissance());

        // Stream sécurisé pour extraire la date de consultation maximale (dernière visite)
        if (patient.getConsultations() != null && !patient.getConsultations().isEmpty()) {
            LocalDate maxDate = patient.getConsultations().stream()
                    .map(c -> c.getDateConsultation())
                    .filter(date -> date != null)
                    .max(LocalDate::compareTo)
                    .orElse(null);
            dto.setLastConsultationDate(maxDate);
        }
        return dto;
    }

    public RapportSummaryPatientsDTO getStatsSummary(String dateDebut, String dateFin, String sexe,
                                                     String situation, String assurance, Integer ageMin,
                                                     Integer ageMax) {

        List<Object[]> results = patientRepository.getStats(dateDebut, dateFin, sexe, situation,
                assurance, ageMin, ageMax);

        if (results == null || results.isEmpty() || results.get(0) == null) {
            return new RapportSummaryPatientsDTO(0L, 0L, 0L, 0L, 0L);
        }

        Object[] row = results.get(0);

        return new RapportSummaryPatientsDTO(
                ((Number) row[0]).longValue(), // total
                ((Number) row[1]).longValue(), // males
                ((Number) row[2]).longValue(), // females
                ((Number) row[3]).longValue(), // insured
                ((Number) row[4]).longValue()  // uninsured
        );
    }

    public RapportSummaryPatientsDTO getStatsSummaryByClient(Long idClient, String dateDebut, String dateFin, String sexe,
                                                             String situation, String assurance, Integer ageMin,
                                                             Integer ageMax) {
        System.out.println("DEBUG: Appel stats pour Client=" + idClient + ", Début=" + dateDebut + ", Fin=" + dateFin);
        RapportSummaryProjection projection = patientRepository.getStatsByClient(idClient, dateDebut, dateFin, sexe, situation,
                assurance, ageMin, ageMax);

        if (projection == null) {
            return new RapportSummaryPatientsDTO(0L, 0L, 0L, 0L, 0L);
        }

        return new RapportSummaryPatientsDTO(
                projection.getTotal() != null ? projection.getTotal() : 0L,
                projection.getMasculin() != null ? projection.getMasculin() : 0L,
                projection.getFeminin() != null ? projection.getFeminin() : 0L,
                projection.getAssures() != null ? projection.getAssures() : 0L,
                projection.getNonAssures() != null ? projection.getNonAssures() : 0L
        );
    }

    public StateOfPatient getInfos(Long idPatint) {
        int consNumber = consultationRepository.countAllByPatientIdPatient(idPatint);
        int docNumber = documentRepository.countAllByPatientIdPatient(idPatint);
        int rdvNumber = rendezVousRepository.countAllByPatientIdPatient(idPatint);
        return new StateOfPatient(consNumber, docNumber, rdvNumber);
    }
}
