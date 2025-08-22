package com.simple_cabinet_medical.Backend.service;

import com.simple_cabinet_medical.Backend.Dto.ConsultationsParMoisDTO;
import com.simple_cabinet_medical.Backend.Dto.RepartitionSexeDTO;
import com.simple_cabinet_medical.Backend.model.Consultation;
import com.simple_cabinet_medical.Backend.model.Patient;
import com.simple_cabinet_medical.Backend.model.RendezVous;
import com.simple_cabinet_medical.Backend.repository.ClientRepository;
import com.simple_cabinet_medical.Backend.repository.ConsultationRepository;
import com.simple_cabinet_medical.Backend.repository.PatientRepository;
import com.simple_cabinet_medical.Backend.repository.RendezVousRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class StatistiqueService {

    private final PatientRepository patientRepository;
    private final ConsultationRepository consultationRepository;
    private final RendezVousRepository rendezVousRepository;

    public StatistiqueService(PatientRepository patientRepository,
                              ConsultationRepository consultationRepository,
                              RendezVousRepository rendezVousRepository) {
        this.patientRepository = patientRepository;
        this.consultationRepository = consultationRepository;
        this.rendezVousRepository = rendezVousRepository;
    }

    public int getTotalPatientsByCleint(Long id) {
        List<Patient> patients = patientRepository.findAllByClientCreatorId(id);
        return patients.size();
    }

    public int getTotalConsultationsByCleint(Long id) {
        List<Consultation> consultations = consultationRepository.findAllByClientCreatorId(id);
        return consultations.size();
    }

    public int getTotalPatientsAuj(Long id) {
        // Récupération des consultations du jour
        List<Consultation> totalConsultation =
                consultationRepository.findAllByClientCreatorIdAndDateConsultation(id, LocalDate.now());
        List<Patient> totalPatientsByCons =
                totalConsultation.stream().map(Consultation::getPatient).toList();

        // Récupération des rendez-vous du jour
        List<RendezVous> totalRendezVous =
                rendezVousRepository.findAllByClientCreatorIdAndDateRendezVous(id, LocalDate.now());
        List<Patient> totalPatientsByRendezVous =
                totalRendezVous.stream().map(RendezVous::getPatient).toList();

        // Fusion sans doublons
        Set<Patient> totalPatients = new HashSet<>();
        totalPatients.addAll(totalPatientsByCons);
        totalPatients.addAll(totalPatientsByRendezVous);

        return totalPatients.size();
    }

    public int getTotaleRendezVousAoujourdhuiByCleint(Long id) {
        List<RendezVous> rendezVousList = rendezVousRepository.findAllByClientCreatorIdAndDateRendezVous(id, LocalDate.now());
        return rendezVousList.size();
    }

    public List<ConsultationsParMoisDTO> getConsultationsParMois(Long idClient) {
        List<Object[]> results = consultationRepository.countConsultationsByMonth(idClient);

        Map<Integer, Long> stats = new LinkedHashMap<>();
        for (int i = 1; i <= 12; i++) {
            stats.put(i, 0L);
        }

        for (Object[] row : results) {
            Integer mois = ((Number) row[0]).intValue();   // Fix casting
            Long total = ((Number) row[1]).longValue();    // Fix casting
            stats.put(mois, total);
        }

        return stats.entrySet().stream()
                .map(e -> new ConsultationsParMoisDTO(e.getKey(), e.getValue()))
                .toList();
    }

    public List<RepartitionSexeDTO> getRepartitionParSexe(Long idClient) {
        List<Object[]> results = patientRepository.countPatientsBySexe(idClient);
        return results.stream()
                .map(r -> new RepartitionSexeDTO((String) r[0], (Long) r[1]))
                .toList();
    }
}
