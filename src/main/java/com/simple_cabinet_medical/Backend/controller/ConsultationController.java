package com.simple_cabinet_medical.Backend.controller;

import com.simple_cabinet_medical.Backend.Dto.DiagnosticDTO;
import com.simple_cabinet_medical.Backend.Dto.ExistingFile;
import com.simple_cabinet_medical.Backend.Dto.RapportSummaryPatientsDTO;
import com.simple_cabinet_medical.Backend.Dto.files.UpdateFilesRequest;
import com.simple_cabinet_medical.Backend.Dto.files.UpdateFilesResponse;
import com.simple_cabinet_medical.Backend.service.ConsultationService;
import com.simple_cabinet_medical.Backend.service.FileStorageService;
import com.simple_cabinet_medical.Backend.service.GoogleStorageService;
import com.simple_cabinet_medical.Backend.service.UtilisateurService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/consultations")
public class ConsultationController {

    private final ConsultationService consultationService;
    private final FileStorageService fileStorageService;
    private final GoogleStorageService googleStorageService;
    private final UtilisateurService utilisateurService;

    public ConsultationController(ConsultationService consultationService, FileStorageService fileStorageService, GoogleStorageService googleStorageService, UtilisateurService utilisateurService) {
        this.consultationService = consultationService;
        this.fileStorageService = fileStorageService;
        this.googleStorageService = googleStorageService;
        this.utilisateurService = utilisateurService;
    }

    @GetMapping("/diagnostics")
    public ResponseEntity<List<DiagnosticDTO>> getDiagnostics(
            @RequestParam Long idClient,
            @RequestParam String diagnosticMedical) {

        List<DiagnosticDTO> diagnostics = consultationService.getDiagnostics(idClient, diagnosticMedical);
        return ResponseEntity.ok(diagnostics);
    }

//    @PostMapping("/upload")
//    public ResponseEntity<?> uploadFiles(
//            @RequestParam("files") List<MultipartFile> files,
//            @RequestParam("idConsultation") Long idConsultation) {
//        try {
//            List<String> paths = fileStorageService.saveConsultationFiles(files, idConsultation);
//            return ResponseEntity.ok(paths);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }

    @GetMapping("/consultation/{idConsultation}/files")
    public ResponseEntity<List<ExistingFile>> getConsultationFiles(
            @PathVariable Long idConsultation,
            Authentication authentication) {
        String username = authentication.getName();
        Long clientId = utilisateurService.getUtilisateurByUserName(username).getClient().getIdClient();
        return ResponseEntity.ok(googleStorageService.listFilesForConsultation(idConsultation, clientId));
    }

    @PostMapping("/update/{idConsultation}")
    public ResponseEntity<?> updateFiles(
            @PathVariable Long idConsultation,
            @RequestBody UpdateFilesRequest request) {
        try {
            UpdateFilesResponse response = consultationService.updateConsultationFiles(
                    idConsultation,
                    request.getDeletedFileNames(),
                    request.getNewFilesMeta()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @DeleteMapping("/V2/{consultationId}")
    public ResponseEntity<Void> deleteConsultation(@PathVariable Long consultationId,
                                                   Authentication authentication){
        String username = authentication.getName();
        Long clientId = utilisateurService.getUtilisateurByUserName(username).getClient().getIdClient();
        consultationService.deleteConsultationById(clientId,consultationId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/get-upload-urls/{idConsultation}")
    public ResponseEntity<Map<String, String>> getUploadUrls(
            @PathVariable Long idConsultation,
            @RequestBody List<String> fileNames,
            Authentication authentication) {

        try {
            String username = authentication.getName();
            Long clientId = utilisateurService.getUtilisateurByUserName(username).getClient().getIdClient();
            Map<String, String> urlMap = new HashMap<>();

            for (String fileName : fileNames) {
                String path = clientId + "/consultations/" + idConsultation + "/" + fileName;
                String url = googleStorageService.generateUploadUrlForCons(path, "application/octet-stream");
                urlMap.put(fileName, url);
            }

            return ResponseEntity.ok(urlMap);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/rapport/stats")
    public ResponseEntity<RapportSummaryPatientsDTO> getRapportPatientsStats(
            @RequestParam Long clientId,
            @RequestParam(required = false) String dateDebut,
            @RequestParam(required = false) String dateFin,
            @RequestParam(required = false) String diagnostic,
            @RequestParam(required = false) String motif,
            @RequestParam(required = false) String examenClinique,
            @RequestParam(required = false) String catEvolution
    ) {
        RapportSummaryPatientsDTO stats = consultationService.getRapportPatientsStats(
                clientId,
                dateDebut,
                dateFin,
                diagnostic,
                motif,
                examenClinique,
                catEvolution
        );

        return ResponseEntity.ok(stats);
    }
}
