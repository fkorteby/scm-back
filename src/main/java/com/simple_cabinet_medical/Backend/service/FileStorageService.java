package com.simple_cabinet_medical.Backend.service;

import com.simple_cabinet_medical.Backend.utils.Storage.FileStorageProperties;
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
public class FileStorageService {

    private final Path fileStorageLocation;
    private static final Logger log = (Logger) LoggerFactory.getLogger(FileStorageService.class);

    public FileStorageService(FileStorageProperties properties) {
        this.fileStorageLocation = Paths.get(properties.getUploadDir())
                .toAbsolutePath()
                .normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Impossible de créer le dossier upload.", ex);
        }
    }

    public String storeClientLogo(MultipartFile file, String clientName) {
        String originalFileName = file.getOriginalFilename();

        if (originalFileName == null) {
            throw new RuntimeException("Le fichier n'a pas de nom.");
        }

        String extension = "";
        int i = originalFileName.lastIndexOf('.');
        if (i > 0) {
            extension = originalFileName.substring(i);
        }

        String cleanClientName = clientName.toLowerCase()
                .replaceAll("\\s+", "_")
                .replaceAll("[^a-zA-Z0-9_]", "");

        String fileName = "client_" + cleanClientName + extension;

        try {
            Path logoDirectory = this.fileStorageLocation.resolve("logos");
            Files.createDirectories(logoDirectory);
            Path targetLocation = logoDirectory.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return "logos/" + fileName;
        } catch (IOException ex) {
            throw new RuntimeException("Impossible d'enregistrer le logo : " + fileName, ex);
        }
    }
    public String storeVisuelCompangePub(MultipartFile file, Long idAnnonceur, Long idCompagnePub) {

        log.info("Début upload visuel campagne - annonceurId={}, campagneId={}", idAnnonceur, idCompagnePub);

        if (file.isEmpty()) {
            log.error("Upload échoué : fichier vide - annonceurId={}, campagneId={}", idAnnonceur, idCompagnePub);
            throw new RuntimeException("Le fichier fourni est vide.");
        }

        String originalFileName = file.getOriginalFilename();

        if (originalFileName == null) {
            log.error("Upload échoué : nom fichier null - annonceurId={}, campagneId={}", idAnnonceur, idCompagnePub);
            throw new RuntimeException("Le fichier n'a pas de nom.");
        }

        log.debug("Nom fichier original : {}", originalFileName);

        String cleanFileName = originalFileName.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");

        log.debug("Nom fichier nettoyé : {}", cleanFileName);

        try {

            Path relativePath = Paths.get("ads", String.valueOf(idAnnonceur), String.valueOf(idCompagnePub));
            log.debug("Chemin relatif généré : {}", relativePath);

            Path targetDirectory = this.fileStorageLocation.resolve(relativePath);
            log.debug("Chemin absolu dossier cible : {}", targetDirectory);

            Files.createDirectories(targetDirectory);
            log.info("Dossier vérifié/créé : {}", targetDirectory);

            Path targetFilePath = targetDirectory.resolve(cleanFileName);
            log.debug("Chemin fichier final : {}", targetFilePath);

            Files.copy(file.getInputStream(), targetFilePath, StandardCopyOption.REPLACE_EXISTING);

            log.info("Upload réussi : {}", targetFilePath);

            String finalPath = relativePath.resolve(cleanFileName).toString().replace("\\", "/");

            log.info("Chemin retourné API : {}", finalPath);

            return finalPath;

        } catch (IOException ex) {

            log.error("Erreur IO upload visuel - annonceurId={}, campagneId={}, message={}",
                    idAnnonceur,
                    idCompagnePub,
                    ex.getMessage(),
                    ex);

            throw new RuntimeException(
                    "Impossible d'enregistrer le visuel pour la campagne ID: " + idCompagnePub,
                    ex
            );
        }
    }

    public Resource loadFileAsResource(String relativePath) {
        try {
            Path filePath = this.fileStorageLocation.resolve(relativePath).normalize();
            if (!filePath.startsWith(this.fileStorageLocation)) {
                throw new RuntimeException("Accès non autorisé au fichier : " + relativePath);
            }
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Fichier introuvable : " + relativePath);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("Erreur de chemin : " + relativePath, ex);
        }
    }

    public List<String> saveConsultationFiles(List<MultipartFile> files, Long idConsultation) throws IOException {

        if (files == null || files.isEmpty()) {
            throw new RuntimeException("No files provided.");
        }

        Path consultationDir = this.fileStorageLocation
                .resolve("consultations")
                .resolve(String.valueOf(idConsultation));
        Files.createDirectories(consultationDir);

        List<String> savedPaths = new ArrayList<>();

        for (MultipartFile file : files) {

            if (file.isEmpty()) {
                throw new RuntimeException("One of the files is empty.");
            }

            if (file.getSize() > 2 * 1024 * 1024) {
                throw new RuntimeException("File '" + file.getOriginalFilename() + "' exceeds 2MB limit.");
            }

            String contentType = file.getContentType();
            if (contentType == null || !(
                    contentType.equals("application/pdf") ||
                            contentType.equals("image/png")       ||
                            contentType.equals("image/jpeg"))) {
                throw new RuntimeException("File '" + file.getOriginalFilename() + "': only PDF, PNG, JPG allowed.");
            }

            String originalFileName = file.getOriginalFilename();
            if (originalFileName == null) {
                throw new RuntimeException("A file has no name.");
            }

            String cleanFileName = originalFileName;
            Path targetFilePath = consultationDir.resolve(cleanFileName);
            Files.copy(file.getInputStream(), targetFilePath, StandardCopyOption.REPLACE_EXISTING);

            String relativePath = Paths.get("consultations", String.valueOf(idConsultation), cleanFileName)
                    .toString().replace("\\", "/");
            savedPaths.add(relativePath);

            log.info("Consultation file saved: {}", targetFilePath);
        }

        return savedPaths;
    }
    public Path getConsultationDir(Long idConsultation) {
        return this.fileStorageLocation
                .resolve("consultations")
                .resolve(String.valueOf(idConsultation));
    }

    public List<String> updateConsultationFilesSmarter(
            Long idConsultation,
            List<MultipartFile> newFiles,
            List<String> existingNames,
            List<String> existingRenames) throws IOException {

        Path consultationDir = getConsultationDir(idConsultation);
        List<String> savedPaths = new ArrayList<>();

        if (!Files.exists(consultationDir)) {
            Files.createDirectories(consultationDir);
        }

        // 1. Récupérer les fichiers actuels sur disque
        List<String> currentFiles = Files.exists(consultationDir)
                ? Files.list(consultationDir)
                .filter(Files::isRegularFile)
                .map(p -> p.getFileName().toString())
                .collect(Collectors.toList())
                : new ArrayList<>();

        // 2. Supprimer les fichiers qui ne sont plus dans existingNames
        List<String> toKeep = existingNames != null ? existingNames : new ArrayList<>();
        for (String current : currentFiles) {
            if (!toKeep.contains(current)) {
                Files.deleteIfExists(consultationDir.resolve(current));
                log.info("Fichier supprimé : {}", current);
            }
        }

        // 3. Renommer les fichiers existants si le nom a changé
        if (existingNames != null && existingRenames != null) {
            for (int i = 0; i < existingNames.size(); i++) {
                String oldName = existingNames.get(i);
                String newName = i < existingRenames.size() ? existingRenames.get(i) : oldName;

                if (!oldName.equals(newName)) {
                    Path oldPath = consultationDir.resolve(oldName);
                    Path newPath = consultationDir.resolve(newName);
                    if (Files.exists(oldPath)) {
                        Files.move(oldPath, newPath, StandardCopyOption.REPLACE_EXISTING);
                        log.info("Fichier renommé : {} → {}", oldName, newName);
                    }
                }
                savedPaths.add("consultations/" + idConsultation + "/" + newName);
            }
        }

        // 4. Ajouter les nouveaux fichiers
        if (newFiles != null) {
            for (MultipartFile file : newFiles) {
                if (file.isEmpty()) continue;

                if (file.getSize() > 2 * 1024 * 1024)
                    throw new RuntimeException("Fichier '" + file.getOriginalFilename() + "' dépasse 2MB.");

                String ct = file.getContentType();
                if (ct == null || !(ct.equals("application/pdf") || ct.equals("image/png") || ct.equals("image/jpeg")))
                    throw new RuntimeException("Format non autorisé : " + file.getOriginalFilename());

                String fileName = file.getOriginalFilename();
                Path target = consultationDir.resolve(fileName);
                Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
                savedPaths.add("consultations/" + idConsultation + "/" + fileName);
                log.info("Nouveau fichier ajouté : {}", target);
            }
        }

        return savedPaths;
    }
}
