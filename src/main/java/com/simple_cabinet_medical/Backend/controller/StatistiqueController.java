package com.simple_cabinet_medical.Backend.controller;

import com.simple_cabinet_medical.Backend.Dto.ConsultationsParMoisDTO;
import com.simple_cabinet_medical.Backend.Dto.RepartitionSexeDTO;
import com.simple_cabinet_medical.Backend.service.StatistiqueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/statistique")
public class StatistiqueController {
    private final StatistiqueService statistiqueService;

    public StatistiqueController(StatistiqueService statistiqueService) {
        this.statistiqueService = statistiqueService;
    }
    @GetMapping("/total-patients/{id}")
    public ResponseEntity<Integer> getTotalPatientsByCleint(@PathVariable Long id) {
        int totalPatients = statistiqueService.getTotalPatientsByCleint(id);
        return ResponseEntity.ok(totalPatients);
    }
    @GetMapping("/total-consultations/{id}")
    public ResponseEntity<Integer> getTotalConsultationsByCleint(@PathVariable Long id) {
        int totalConsultations = statistiqueService.getTotalConsultationsByCleint(id);
        return ResponseEntity.ok(totalConsultations);
    }
    @GetMapping("/total-rendezvous-today/{id}")
    public ResponseEntity<Integer> getTotalRendezVousAujourdhuiByCleint(@PathVariable Long id) {
        int totalRendezVousToday = statistiqueService.getTotaleRendezVousAoujourdhuiByCleint(id);
        return ResponseEntity.ok(totalRendezVousToday);
    }
    @GetMapping("/total-patients-today/{id}")
    public ResponseEntity<Integer> getTotalPatientsAujourdhui(@PathVariable Long id) {
        int totalPatientsToday = statistiqueService.getTotalPatientsAuj(id);
        return ResponseEntity.ok(totalPatientsToday);
    }
    @GetMapping("/stats/{idClient}")
    public List<ConsultationsParMoisDTO> getConsultationsParMois(@PathVariable Long idClient) {
        return statistiqueService.getConsultationsParMois(idClient);
    }
    @GetMapping("/repartition-sexe/{idClient}")
    public List<RepartitionSexeDTO> getRepartitionParSexe(@PathVariable Long idClient) {
        return statistiqueService.getRepartitionParSexe(idClient);
    }
}
