package com.simple_cabinet_medical.Backend.service;

import com.simple_cabinet_medical.Backend.Dto.Dash.*;
import com.simple_cabinet_medical.Backend.Mapper.Dash.DashMapper;
import com.simple_cabinet_medical.Backend.model.Consultation;
import com.simple_cabinet_medical.Backend.model.Patient;
import com.simple_cabinet_medical.Backend.model.RendezVous;
import com.simple_cabinet_medical.Backend.repository.ConsultationRepository;
import com.simple_cabinet_medical.Backend.repository.PatientRepository;
import com.simple_cabinet_medical.Backend.repository.RendezVousRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatistiqueService {

    private final PatientRepository patientRepository;
    private final ConsultationRepository consultationRepository;
    private final RendezVousRepository rendezVousRepository;
    private final DashMapper dashMapper;

    public StatistiqueService(PatientRepository patientRepository,
                              ConsultationRepository consultationRepository,
                              RendezVousRepository rendezVousRepository, DashMapper dashMapper) {
        this.patientRepository = patientRepository;
        this.consultationRepository = consultationRepository;
        this.rendezVousRepository = rendezVousRepository;
        this.dashMapper = dashMapper;
    }

    public Long getTotalPatientsByCleint(Long id) {
        return patientRepository.nombrePatientByClient(id);
    }

    public int getTotaleRendezVousAoujourdhuiByCleint(Long id) {
        return rendezVousRepository.countRendezVousOfTodayByClientCreatorId(id, LocalDate.now())
                .orElse(0L).intValue();
    }
    public int getTotalConsultationsByCleint(Long id) {
        return consultationRepository.countByClientCreatorId(id).orElse(0L).intValue();
    }

    public int getTotalPatientsAuj(Long id) {
        // Récupération des consultations du jour
        List<Consultation> totalConsultation =
                consultationRepository.findAllByClientCreatorIdAndDateConsultationOrderByDateConsultationDesc(id, LocalDate.now());
        List<Patient> totalPatientsByCons =
                totalConsultation.stream().map(Consultation::getPatient).toList();

        // Récupération des rendez-vous du jour
//        List<RendezVous> totalRendezVous =
//                rendezVousRepository.findAllByClientCreatorIdAndDateRendezVous(id, LocalDate.now());
//        List<Patient> totalPatientsByRendezVous =
//                totalRendezVous.stream().map(RendezVous::getPatient).toList();

        // Fusion sans doublons
//        Set<Patient> totalPatients = new HashSet<>();
//        totalPatients.addAll(totalPatientsByCons);
//        totalPatients.addAll(totalPatientsByRendezVous);

        return totalPatientsByCons.size();
    }
    public List<Object[]> getConsultationsParMois(Long idClient) {
        return  consultationRepository.getConsultationsParMois(idClient);
    }

    public List<RepartitionSexeDTO> getRepartitionParSexe(Long idClient) {
        List<Object[]> results = patientRepository.countPatientsBySexe(idClient);

        // Convert query result into a Map for easy lookup
        Map<String, Long> sexeCounts = results.stream()
                .collect(Collectors.toMap(
                        r -> (String) r[0],
                        r -> (Long) r[1]
                ));

        // Always include both Homme and Femme
        List<RepartitionSexeDTO> repartition = new ArrayList<>();
        repartition.add(new RepartitionSexeDTO("Masculin", sexeCounts.getOrDefault("Masculin", 0L)));
        repartition.add(new RepartitionSexeDTO("Féminin", sexeCounts.getOrDefault("Féminin", 0L)));

        return repartition;
    }


    public List<Object[]> getConsultationsParJour(Long idClient, String period) {
        LocalDate startDate;
        LocalDate now = LocalDate.now();

        switch (period.toLowerCase()) {
            case "week":
                startDate = now.minusWeeks(1);
                break;
            case "month":
                startDate = now.minusMonths(1);
                break;
            case "3months":
                startDate = now.minusMonths(3);
                break;
            case "year":
                startDate = now.minusYears(1);
                break;
            default:
                startDate = now.minusMonths(1);
        }

        return consultationRepository.getConsultationsParJour(idClient, startDate);
    }
    public List<PatientAujhDto> getPatientAujh(Long idClient){
        List<Consultation> consultations=this.consultationRepository
                .findAllByClientCreatorIdAndDateConsultationOrderByDateConsultationDesc(idClient,LocalDate.now());
        List<PatientAujhDto> patientAujhDtos=consultations.stream().map(consultation ->
                dashMapper.patientDtoFromConsultation(consultation)).collect(Collectors.toList());
        return patientAujhDtos;
    }
    public List<RenderVousAujhDto> getRendezVousAujh(Long idClient){
        List<RendezVous> rendezVous=this.rendezVousRepository.findAllByClientCreatorIdAndDateRendezVous(idClient, LocalDate.now());
        List<RenderVousAujhDto> renderVousAujhDtos=rendezVous.stream().map(rendezVous1 ->
                dashMapper.rendezVousDtoFromRendezVous(rendezVous1)).collect(Collectors.toList());
        return renderVousAujhDtos;
    }
    public List<Object> getTop10DiagnosticMedical(Long idClient) {
        List<Object> results = consultationRepository.getTop10DiagnosticMedical(idClient);
        return results;
    }

}
