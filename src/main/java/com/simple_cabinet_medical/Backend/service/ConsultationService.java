package com.simple_cabinet_medical.Backend.service;

import com.simple_cabinet_medical.Backend.Dto.DiagnosticDTO;
import com.simple_cabinet_medical.Backend.Dto.RapportSummaryPatientsDTO;
import com.simple_cabinet_medical.Backend.Dto.RapportSummaryProjection;
import com.simple_cabinet_medical.Backend.Dto.files.NewFileMeta;
import com.simple_cabinet_medical.Backend.Dto.files.UpdateFilesResponse;
import com.simple_cabinet_medical.Backend.Dto.files.UploadUrlEntry;
import com.simple_cabinet_medical.Backend.Permission.UtilisateurPermision;
import com.simple_cabinet_medical.Backend.model.Consultation;
import com.simple_cabinet_medical.Backend.model.Utilisateur;
import com.simple_cabinet_medical.Backend.repository.ConsultationRepository;
import jakarta.transaction.Transactional;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;

@Service
public class ConsultationService {
    private final ConsultationRepository consultationRepository;
    private final UtilisateurPermision utilisateurPermision;
    private final GoogleStorageService googleStorageService;

    private static final Logger log = (Logger) LoggerFactory.getLogger(ConsultationService.class);

    public ConsultationService(ConsultationRepository consultationRepository, UtilisateurPermision utilisateurPermision, GoogleStorageService googleStorageService) {
        this.consultationRepository = consultationRepository;
        this.utilisateurPermision = utilisateurPermision;
        this.googleStorageService = googleStorageService;
    }

    public List<DiagnosticDTO> getDiagnostics(Long idClient, String diagnosticMedical) {
        Utilisateur utilisateur = utilisateurPermision.getCurrentUser();
        Long userClientId = utilisateur.getClient().getIdClient();
        if (!userClientId.equals(idClient)) {
            throw new SecurityException("Access denied: You do not have permission to access this client's diagnostics.");
        }
        Pageable pageable = PageRequest.of(0, 10);
        return consultationRepository.getDiagnosticMedical(idClient, diagnosticMedical,pageable);
    }

    public RapportSummaryPatientsDTO getRapportPatientsStats(
            Long clientId,
            String dateDebut,
            String dateFin,
            String diagnostic,
            String motif,
            String examenClinique,
            String catEvolution
    ) {
        log.info("🔍 [STATS] Début de la récupération des stats - clientId: {}, dateDebut: {}, dateFin: {}, diagnostic: {}, motif: {}, examenClinique: {}, catEvolution: {}", clientId, dateDebut, dateFin, diagnostic, motif, examenClinique, catEvolution);

        RapportSummaryProjection projection = consultationRepository.getRapportPatientsStats(
                clientId, dateDebut, dateFin, diagnostic, motif, examenClinique, catEvolution
        );

        if (projection == null) {
            log.info("⚠️ [STATS] La projection retournée par la base de données est NULL pour le clientId: {}", clientId);
            return new RapportSummaryPatientsDTO(0L, 0L, 0L, 0L, 0L);
        }

        log.info("📊 [STATS] Projection brute reçue - Total: {}, Masculin: {}, Féminin: {}, Assurés: {}, Non assurés: {}",
                projection.getTotal(),
                projection.getMasculin(),
                projection.getFeminin(),
                projection.getAssures(),
                projection.getNonAssures());

        if (projection.getTotal() == null) {
            log.warn("⚠️ [STATS] projection.getTotal() est NULL. Retour de zéros par sécurité.");
            return new RapportSummaryPatientsDTO(0L, 0L, 0L, 0L, 0L);
        }

        return new RapportSummaryPatientsDTO(
                projection.getTotal(),
                projection.getMasculin(),
                projection.getFeminin(),
                projection.getAssures(),
                projection.getNonAssures()
        );
    }

    @Transactional
    public void deleteConsultationById(Long clientId, Long consultationId){
        consultationRepository.deleteConsultationByIdConsultation(consultationId);
        googleStorageService.deleteFilesByConsultation(clientId,consultationId);
    }

    public UpdateFilesResponse updateConsultationFiles(
            Long idConsultation,
            List<String> deletedFileNames,
            List<NewFileMeta> newFilesMeta) {

        Consultation consultation = consultationRepository.findById(idConsultation)
                .orElseThrow(() -> new RuntimeException("Consultation introuvable: " + idConsultation));

        Long clientId = consultation.getClient().getIdClient();

        // 1. Suppression des fichiers marqués supprimés
        if (deletedFileNames != null) {
            for (String fileName : deletedFileNames) {
                String path = buildFilePath(clientId, idConsultation, fileName);
                googleStorageService.deleteConsFiles(path);
            }
        }

        // 2. Génération d'URLs signées pour les nouveaux fichiers
        List<UploadUrlEntry> uploadUrls = new ArrayList<>();
        if (newFilesMeta != null) {
            for (NewFileMeta meta : newFilesMeta) {
                String path = buildFilePath(clientId, idConsultation, meta.getFileName());
                String signedUrl = googleStorageService.generateUploadUrlForCons(path, meta.getContentType());
                uploadUrls.add(new UploadUrlEntry(meta.getFileName(), signedUrl));
            }
        }

        return new UpdateFilesResponse(uploadUrls);
    }

    private String buildFilePath(Long clientId, Long idConsultation, String fileName) {
        return clientId + "/consultations/" + idConsultation + "/" + fileName;
    }
}
