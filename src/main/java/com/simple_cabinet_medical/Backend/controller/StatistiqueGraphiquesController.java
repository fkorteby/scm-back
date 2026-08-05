package com.simple_cabinet_medical.Backend.controller;

import com.simple_cabinet_medical.Backend.Dto.Dash.RepartitionSexeDTO;
import com.simple_cabinet_medical.Backend.repository.ConsultationRepository;
import com.simple_cabinet_medical.Backend.repository.PatientRepository;
import com.simple_cabinet_medical.Backend.repository.RendezVousRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/statistique")
public class StatistiqueGraphiquesController {

    private final ConsultationRepository consultationRepository;
    private final PatientRepository      patientRepository;
    private final RendezVousRepository   rendezVousRepository;

    public StatistiqueGraphiquesController(ConsultationRepository consultationRepository,
                                           PatientRepository      patientRepository,
                                           RendezVousRepository   rendezVousRepository) {
        this.consultationRepository = consultationRepository;
        this.patientRepository      = patientRepository;
        this.rendezVousRepository   = rendezVousRepository;
    }

    @GetMapping("/consultations/jour-periode/{clientId}")
    public List<Object[]> getConsultationsParJourPeriode(
            @PathVariable Long clientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {

        return consultationRepository.countConsultationsParJourPeriode(clientId, dateDebut, dateFin);
    }

    @GetMapping("/consultations/mois-periode/{clientId}")
    public List<Object[]> getConsultationsParMoisPeriode(
            @PathVariable Long clientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {

        return consultationRepository.countConsultationsParMoisPeriode(clientId, dateDebut, dateFin);
    }

    @GetMapping("/repartition-sexe-periode/{clientId}")
    public List<RepartitionSexeDTO> getSexDistributionPeriode(
            @PathVariable Long clientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {

        return patientRepository.countConsultationsBySexePeriodeRaw(clientId, dateDebut, dateFin)
                .stream()
                .map(row -> new RepartitionSexeDTO(
                        (String) row[0],
                        ((Number) row[1]).longValue()
                ))
                .collect(Collectors.toList());
    }

    @GetMapping("/rendezvous/jour-periode/{clientId}")
    public List<Object[]> getRendezVousParJourPeriode(
            @PathVariable Long clientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {

        return rendezVousRepository.countRendezVousParJourPeriode(clientId, dateDebut, dateFin);
    }

    @GetMapping("/diagnostic-periode/{clientId}")
    public List<Object[]> getTopPathologiesPeriode(
            @PathVariable Long clientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        return consultationRepository.countTopDiagnosticsPeriode(clientId, dateDebut, dateFin);
    }
}