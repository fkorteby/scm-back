package com.simple_cabinet_medical.Backend.controller;

import com.simple_cabinet_medical.Backend.Dto.adminDash.*;
import com.simple_cabinet_medical.Backend.service.AdminDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/kpis")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    public AdminDashboardController(AdminDashboardService adminDashboardService) {
        this.adminDashboardService = adminDashboardService;
    }

    @GetMapping("/doctors")
    public ResponseEntity<DoctorKpiDto> getDoctorsKpi() {
        DoctorKpiDto kpi = adminDashboardService.getDoctorsKpi();
        return ResponseEntity.ok(kpi);
    }
    @GetMapping("/patients")
    public ResponseEntity<PatientKpiDto> getPatientsKpi() {
        PatientKpiDto kpi = adminDashboardService.getPatientsKpi();
        return ResponseEntity.ok(kpi);
    }

    @GetMapping("/consultations")
    public ResponseEntity<ConsultationCardDto> getConsultationCard() {
        ConsultationCardDto card = adminDashboardService.getConsultationCard();
        return ResponseEntity.ok(card);
    }

    @GetMapping("/doctors-evolution")
    public ResponseEntity<DoctorEvolutionDto> getDoctorEvolution() {
        DoctorEvolutionDto evolution = adminDashboardService.getDoctorEvolution();
        return ResponseEntity.ok(evolution);
    }

    @GetMapping("/geographic-distribution")
    public ResponseEntity<GeoKpiResponseDto> getGeoStatistics() {
        GeoKpiResponseDto response = adminDashboardService.getGeoStatistics();
        return ResponseEntity.ok(response);
    }
}