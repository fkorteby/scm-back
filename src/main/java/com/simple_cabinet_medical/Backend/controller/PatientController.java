package com.simple_cabinet_medical.Backend.controller;

import com.simple_cabinet_medical.Backend.Dto.PatientResponseDto;
import com.simple_cabinet_medical.Backend.Dto.PatientsRapportDTO;
import com.simple_cabinet_medical.Backend.Dto.RapportSummaryPatientsDTO;
import com.simple_cabinet_medical.Backend.Dto.StateOfPatient;
import com.simple_cabinet_medical.Backend.service.PatientService;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault; // <-- IMPORT MANQUANT
import org.springframework.data.web.PagedResourcesAssembler; // <-- IMPORT MANQUANT
import org.springframework.hateoas.EntityModel; // <-- IMPORT MANQUANT
import org.springframework.hateoas.PagedModel; // <-- IMPORT MANQUANT
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1/patients")
public class PatientController {

    private final PatientService patientService;
    private final PagedResourcesAssembler<PatientsRapportDTO> pagedAssembler; // <-- REQUIS POUR toModel()
    private static final Logger logger = LoggerFactory.getLogger(PatientController.class);

    // Injection via le constructeur des dépendances nécessaires
    public PatientController(PatientService patientService,
                             PagedResourcesAssembler<PatientsRapportDTO> pagedAssembler) {
        this.patientService = patientService;
        this.pagedAssembler = pagedAssembler;
    }

    @GetMapping("/rendezvous-today")
    public ResponseEntity<Page<PatientResponseDto>> findPatientsWithRendezVousToday(
            Long idClient,
            Date date,
            int page,
            int size) {
        Page<PatientResponseDto> patients = patientService.findPatientsWithRendezVousToday(idClient, date, page, size);
        return ResponseEntity.ok(patients);
    }

    @GetMapping("/rapport")
    public ResponseEntity<PagedModel<EntityModel<PatientsRapportDTO>>> getRapportByClient(
            @RequestParam("clientId") Long clientId,
            @RequestParam(value = "dateDebut", required = false) String dateDebut,
            @RequestParam(value = "dateFin", required = false) String dateFin,
            @RequestParam(value = "sexe", required = false) String sexe,
            @RequestParam(value = "situation", required = false) String situation,
            @RequestParam(value = "assurance", required = false) String assurance,
            @RequestParam(value = "ageMin", required = false) Integer ageMin,
            @RequestParam(value = "ageMax", required = false) Integer ageMax,
            @PageableDefault(size = 20, sort = "nom") Pageable pageable) {

        Page<PatientsRapportDTO> dtoPage = patientService.getRapportByClient(
                clientId, dateDebut, dateFin, sexe, situation, assurance, ageMin, ageMax, pageable);

        return ResponseEntity.ok(pagedAssembler.toModel(dtoPage));
    }

    @GetMapping("/rapportAll")
    public ResponseEntity<PagedModel<EntityModel<PatientsRapportDTO>>> getRapportAll(
            @RequestParam(value = "dateDebut", required = false) String dateDebut,
            @RequestParam(value = "dateFin", required = false) String dateFin,
            @RequestParam(value = "sexe", required = false) String sexe,
            @RequestParam(value = "situation", required = false) String situation,
            @RequestParam(value = "assurance", required = false) String assurance,
            @RequestParam(value = "ageMin", required = false) Integer ageMin,
            @RequestParam(value = "ageMax", required = false) Integer ageMax,
            @PageableDefault(size = 20, sort = "nom") Pageable pageable) {

        Page<PatientsRapportDTO> dtoPage = patientService.getRapportAll(
                dateDebut, dateFin, sexe, situation, assurance, ageMin, ageMax, pageable);

        return ResponseEntity.ok(pagedAssembler.toModel(dtoPage));
    }
    @GetMapping("/rapportForExport")
    public ResponseEntity<List<PatientsRapportDTO>> getRapportByClientForExport(
            @RequestParam("clientId") Long clientId,
            @RequestParam(value = "dateDebut", required = false) String dateDebut,
            @RequestParam(value = "dateFin", required = false) String dateFin,
            @RequestParam(value = "sexe", required = false) String sexe,
            @RequestParam(value = "situation", required = false) String situation,
            @RequestParam(value = "assurance", required = false) String assurance,
            @RequestParam(value = "ageMin", required = false) Integer ageMin,
            @RequestParam(value = "ageMax", required = false) Integer ageMax) {

        List<PatientsRapportDTO> dtoPage = patientService.getRapportByClientForExport(
                clientId, dateDebut, dateFin, sexe, situation, assurance, ageMin, ageMax);

        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/rapportAllForExport")
    public ResponseEntity<List<PatientsRapportDTO>> getRapportAllForExport(
            @RequestParam(value = "dateDebut", required = false) String dateDebut,
            @RequestParam(value = "dateFin", required = false) String dateFin,
            @RequestParam(value = "sexe", required = false) String sexe,
            @RequestParam(value = "situation", required = false) String situation,
            @RequestParam(value = "assurance", required = false) String assurance,
            @RequestParam(value = "ageMin", required = false) Integer ageMin,
            @RequestParam(value = "ageMax", required = false) Integer ageMax) {

        List<PatientsRapportDTO> dtoPage = patientService.getRapportAllForExport(
                dateDebut, dateFin, sexe, situation, assurance, ageMin, ageMax);

        return ResponseEntity.ok(dtoPage);
    }
    @GetMapping("/rapport/stats")
    public ResponseEntity<RapportSummaryPatientsDTO> getStats(
            @RequestParam(value = "dateDebut", required = false) String dateDebut,
            @RequestParam(value = "dateFin", required = false) String dateFin,
            @RequestParam(value = "sexe", required = false) String sexe,
            @RequestParam(value = "situation", required = false) String situation,
            @RequestParam(value = "assurance", required = false) String assurance,
            @RequestParam(value = "ageMin", required = false) Integer ageMin,
            @RequestParam(value = "ageMax", required = false) Integer ageMax) {

        // Appel direct à la méthode de calcul du service
        RapportSummaryPatientsDTO stats = patientService.getStatsSummary(
                dateDebut, dateFin, sexe, situation, assurance, ageMin, ageMax);

        return ResponseEntity.ok(stats);
    }
    @GetMapping("/rapport/statsByClient")
    public ResponseEntity<RapportSummaryPatientsDTO> getStatsByClient(
            @RequestParam(value = "dateDebut", required = false) String dateDebut,
            @RequestParam(value = "clientId", required = false) Long idClient,
            @RequestParam(value = "dateFin", required = false) String dateFin,
            @RequestParam(value = "sexe", required = false) String sexe,
            @RequestParam(value = "situation", required = false) String situation,
            @RequestParam(value = "assurance", required = false) String assurance,
            @RequestParam(value = "ageMin", required = false) Integer ageMin,
            @RequestParam(value = "ageMax", required = false) Integer ageMax) {

        // Appel direct à la méthode de calcul du service
        RapportSummaryPatientsDTO stats = patientService.getStatsSummaryByClient(idClient,
                dateDebut, dateFin, sexe, situation, assurance, ageMin, ageMax);

        return ResponseEntity.ok(stats);
    }
    @GetMapping("extraInfos/{idPatient}")
    public ResponseEntity<StateOfPatient> getExtraInfos(@PathVariable Long idPatient){
        StateOfPatient stateOfPatient = patientService.getInfos(idPatient);
        return new ResponseEntity<>(stateOfPatient, HttpStatus.OK);
    }
}