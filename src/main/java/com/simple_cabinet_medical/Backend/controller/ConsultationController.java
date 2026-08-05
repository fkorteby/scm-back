package com.simple_cabinet_medical.Backend.controller;

import com.simple_cabinet_medical.Backend.Dto.DiagnosticDTO;
import com.simple_cabinet_medical.Backend.Dto.ExistingFile;
import com.simple_cabinet_medical.Backend.service.ConsultationService;
import com.simple_cabinet_medical.Backend.service.FileStorageService;
import com.simple_cabinet_medical.Backend.service.GoogleStorageService;
import org.springframework.http.ResponseEntity;
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

    public ConsultationController(ConsultationService consultationService, FileStorageService fileStorageService, GoogleStorageService googleStorageService) {
        this.consultationService = consultationService;
        this.fileStorageService = fileStorageService;
        this.googleStorageService = googleStorageService;
    }

    @GetMapping("/diagnostics")
    public ResponseEntity<List<DiagnosticDTO>> getDiagnostics(
            @RequestParam Long idClient,
            @RequestParam String diagnosticMedical) {

        List<DiagnosticDTO> diagnostics = consultationService.getDiagnostics(idClient, diagnosticMedical);
        return ResponseEntity.ok(diagnostics);
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFiles(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("idConsultation") Long idConsultation) {
        try {
            List<String> paths = fileStorageService.saveConsultationFiles(files, idConsultation);
            return ResponseEntity.ok(paths);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/consultation/{idConsultation}/files")
    public ResponseEntity<List<ExistingFile>> getConsultationFiles(@PathVariable Long idConsultation) {
        return ResponseEntity.ok(googleStorageService.listFilesForConsultation(idConsultation));
    }

    @PostMapping("/update/{idConsultation}")
    public ResponseEntity<List<String>> updateFiles(
            @PathVariable Long idConsultation,
            @RequestParam(value = "newFiles", required = false) List<MultipartFile> newFiles,
            @RequestParam(value = "existingNames", required = false) List<String> existingNames,
            @RequestParam(value = "existingRenames", required = false) List<String> existingRenames) {
        try {
            List<String> paths = fileStorageService.updateConsultationFilesSmarter(
                    idConsultation, newFiles, existingNames, existingRenames
            );
            return ResponseEntity.ok(paths);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(List.of(e.getMessage()));
        }
    }

    @PostMapping("/get-upload-urls/{idConsultation}")
    public ResponseEntity<Map<String, String>> getUploadUrls(
            @PathVariable Long idConsultation,
            @RequestBody List<String> fileNames) {

        try {
            Map<String, String> urlMap = new HashMap<>();

            for (String fileName : fileNames) {
                // Construit le chemin conforme à votre structure
                String path = "consultations/" + idConsultation + "/" + fileName;
                // Génère l'URL pour ce fichier spécifique
                String url = googleStorageService.generateUploadUrlForCons(path, "application/octet-stream");
                urlMap.put(fileName, url);
            }

            return ResponseEntity.ok(urlMap);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
