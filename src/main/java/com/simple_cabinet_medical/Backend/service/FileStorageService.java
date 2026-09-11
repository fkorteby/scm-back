package com.simple_cabinet_medical.Backend.service;

import com.simple_cabinet_medical.Backend.utils.Storage.FileStorageProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import java.net.MalformedURLException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class FileStorageService {

//    private final String fileStorageLocation;
    private static final Logger log = (Logger) LoggerFactory.getLogger(FileStorageService.class);

//    public FileStorageService(FileStorageProperties properties) {
//        this.fileStorageLocation = Paths.get(properties.getUploadDir())
//                .toAbsolutePath()
//                .normalize();
//
//        try {
//            Files.createDirectories(this.fileStorageLocation);
//        } catch (Exception ex) {
//            throw new RuntimeException("Impossible de créer le dossier upload.", ex);
//        }
//    }

//    public String storeVisuelCompangePub(MultipartFile file, Long idAnnonceur, Long idCompagnePub) {
//
//        log.info("Début upload visuel campagne - annonceurId={}, campagneId={}", idAnnonceur, idCompagnePub);
//
//        if (file.isEmpty()) {
//            log.error("Upload échoué : fichier vide - annonceurId={}, campagneId={}", idAnnonceur, idCompagnePub);
//            throw new RuntimeException("Le fichier fourni est vide.");
//        }
//
//        String originalFileName = file.getOriginalFilename();
//
//        if (originalFileName == null) {
//            log.error("Upload échoué : nom fichier null - annonceurId={}, campagneId={}", idAnnonceur, idCompagnePub);
//            throw new RuntimeException("Le fichier n'a pas de nom.");
//        }
//
//        log.debug("Nom fichier original : {}", originalFileName);
//
//        String cleanFileName = originalFileName.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");
//
//        log.debug("Nom fichier nettoyé : {}", cleanFileName);
//
//        try {
//
//            Path relativePath = Paths.get("ads", String.valueOf(idAnnonceur), String.valueOf(idCompagnePub));
//            log.debug("Chemin relatif généré : {}", relativePath);
//
//            Path targetDirectory = this.fileStorageLocation.resolve(relativePath);
//            log.debug("Chemin absolu dossier cible : {}", targetDirectory);
//
//            Files.createDirectories(targetDirectory);
//            log.info("Dossier vérifié/créé : {}", targetDirectory);
//
//            Path targetFilePath = targetDirectory.resolve(cleanFileName);
//            log.debug("Chemin fichier final : {}", targetFilePath);
//
//            Files.copy(file.getInputStream(), targetFilePath, StandardCopyOption.REPLACE_EXISTING);
//
//            log.info("Upload réussi : {}", targetFilePath);
//
//            String finalPath = relativePath.resolve(cleanFileName).toString().replace("\\", "/");
//
//            log.info("Chemin retourné API : {}", finalPath);
//
//            return finalPath;
//
//        } catch (IOException ex) {
//
//            log.error("Erreur IO upload visuel - annonceurId={}, campagneId={}, message={}",
//                    idAnnonceur,
//                    idCompagnePub,
//                    ex.getMessage(),
//                    ex);
//
//            throw new RuntimeException(
//                    "Impossible d'enregistrer le visuel pour la campagne ID: " + idCompagnePub,
//                    ex
//            );
//        }
//    }

//    public Resource loadFileAsResource(String relativePath) {
//        try {
//            Path filePath = this.fileStorageLocation.resolve(relativePath).normalize();
//            if (!filePath.startsWith(this.fileStorageLocation)) {
//                throw new RuntimeException("Accès non autorisé au fichier : " + relativePath);
//            }
//            Resource resource = new UrlResource(filePath.toUri());
//            if (resource.exists() && resource.isReadable()) {
//                return resource;
//            } else {
//                throw new RuntimeException("Fichier introuvable : " + relativePath);
//            }
//        } catch (MalformedURLException ex) {
//            throw new RuntimeException("Erreur de chemin : " + relativePath, ex);
//        }
//    }

//    public List<String> saveConsultationFiles(List<MultipartFile> files, Long idConsultation) throws IOException {
//
//        if (files == null || files.isEmpty()) {
//            throw new RuntimeException("No files provided.");
//        }
//
//        Path consultationDir = this.fileStorageLocation
//                .resolve("consultations")
//                .resolve(String.valueOf(idConsultation));
//        Files.createDirectories(consultationDir);
//
//        List<String> savedPaths = new ArrayList<>();
//
//        for (MultipartFile file : files) {
//
//            if (file.isEmpty()) {
//                throw new RuntimeException("One of the files is empty.");
//            }
//
//            if (file.getSize() > 2 * 1024 * 1024) {
//                throw new RuntimeException("File '" + file.getOriginalFilename() + "' exceeds 2MB limit.");
//            }
//
//            String contentType = file.getContentType();
//            if (contentType == null || !(
//                    contentType.equals("application/pdf") ||
//                            contentType.equals("image/png")       ||
//                            contentType.equals("image/jpeg"))) {
//                throw new RuntimeException("File '" + file.getOriginalFilename() + "': only PDF, PNG, JPG allowed.");
//            }
//
//            String originalFileName = file.getOriginalFilename();
//            if (originalFileName == null) {
//                throw new RuntimeException("A file has no name.");
//            }
//
//            String cleanFileName = originalFileName;
//            Path targetFilePath = consultationDir.resolve(cleanFileName);
//            Files.copy(file.getInputStream(), targetFilePath, StandardCopyOption.REPLACE_EXISTING);
//
//            String relativePath = Paths.get("consultations", String.valueOf(idConsultation), cleanFileName)
//                    .toString().replace("\\", "/");
//            savedPaths.add(relativePath);
//
//            log.info("Consultation file saved: {}", targetFilePath);
//        }
//
//        return savedPaths;
//    }
}
