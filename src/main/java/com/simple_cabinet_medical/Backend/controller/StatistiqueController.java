package com.simple_cabinet_medical.Backend.controller;

import com.simple_cabinet_medical.Backend.Dto.Dash.*;
import com.simple_cabinet_medical.Backend.service.StatistiqueService;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/statistique")
@PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN','MEDECIN_PRINCIPAL')")
public class StatistiqueController {
    private final StatistiqueService statistiqueService;

    public StatistiqueController(StatistiqueService statistiqueService) {
        this.statistiqueService = statistiqueService;
    }
    @GetMapping("/total-patients/{id}")
    public ResponseEntity<Long> getTotalPatientsByCleint(@PathVariable Long id) {
        Long totalPatients = statistiqueService.getTotalPatientsByCleint(id);
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
    @GetMapping("consultations/mois/{idClient}")
    public List<Object[]> getConsultationsParMois(@PathVariable Long idClient) {
        return statistiqueService.getConsultationsParMois(idClient);
    }
    @GetMapping("consultations/jour/{idClient}")
    public List<Object[]> getConsultationsParJour(
            @PathVariable Long idClient,
            @RequestParam(defaultValue = "month") String period
    ) {
        return statistiqueService.getConsultationsParJour(idClient, period);
    }
    @GetMapping("/repartition-sexe/{idClient}")
    public List<RepartitionSexeDTO> getRepartitionParSexe(@PathVariable Long idClient) {
        return statistiqueService.getRepartitionParSexe(idClient);
    }
    @GetMapping("/patients/{idClient}")
    public ResponseEntity<List<PatientAujhDto>> getTotalPatientsAuhj(@PathVariable Long idClient) {
        List<PatientAujhDto> patientAujh = statistiqueService.getPatientAujh(idClient);
        return ResponseEntity.ok(patientAujh);
    }
    @GetMapping("/rendezvous/{idClient}")
    public ResponseEntity<List<RenderVousAujhDto>> getTotalRendezVousAuhj(@PathVariable Long idClient) {
        List<RenderVousAujhDto> renderVousAujh = statistiqueService.getRendezVousAujh(idClient);
        return ResponseEntity.ok(renderVousAujh);
    }
    @GetMapping("/diagnostic/{idClient}")
        public ResponseEntity<List<Object>> getTop10DiagnosticMedical(@PathVariable Long idClient) {
        List<Object> diagnosticDTOS = statistiqueService.getTop10DiagnosticMedical(idClient);
        return ResponseEntity.ok(diagnosticDTOS);
    }
}
