package com.simple_cabinet_medical.Backend.service;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.*;
import com.simple_cabinet_medical.Backend.Dto.ExistingFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class GoogleStorageService {

    private static final Logger log = LoggerFactory.getLogger(GoogleStorageService.class);
    private final Storage storage;

    private final String bucketName = "scm-logos-prod";
    private final String bucketName1 = "scm-medical-files-prod";


    public GoogleStorageService(Storage storage) {
        this.storage = storage;
    }

    public String upload(MultipartFile file, String path) throws IOException {

        if (file == null || file.isEmpty()) {
            log.warn("Upload failed: file is null or empty");
            throw new IllegalArgumentException("File cannot be null or empty");
        }

        if (path == null || path.trim().isEmpty()) {
            log.warn("Upload failed: invalid path");
            throw new IllegalArgumentException("Path cannot be null or empty");
        }

        try {
            log.info("Starting file upload: {}, destination path: {}",
                    file.getOriginalFilename(), path);

            BlobId blobId = BlobId.of(bucketName, path);

            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(file.getContentType())
                    .build();

            storage.create(blobInfo, file.getBytes());

            log.info("File uploaded successfully: {}", path);

            return path;

        } catch (IOException e) {
            log.error("Error while uploading file: {}", path, e);
            throw e;
        }
    }


    public byte[] download(String path) {

        if (path == null || path.trim().isEmpty()) {
            log.warn("Download failed: invalid path");
            throw new IllegalArgumentException("Path cannot be null or empty");
        }

        try {
            log.info("Starting file download: {}", path);

            Blob blob = storage.get(bucketName, path);

            if (blob == null) {
                log.warn("File not found in bucket: {}", path);
                throw new RuntimeException("File not found");
            }

            byte[] content = blob.getContent();

            log.info("File downloaded successfully: {}, size: {} bytes",
                    path, content.length);

            return content;

        } catch (Exception e) {
            log.error("Error while downloading file: {}", path, e);
            throw e;
        }
    }


    public void delete(String path) {

        if (path == null || path.trim().isEmpty()) {
            log.warn("Delete failed: invalid path");
            throw new IllegalArgumentException("Path cannot be null or empty");
        }

        try {
            log.info("Starting file deletion: {}", path);

            boolean deleted = storage.delete(bucketName, path);

            if (deleted) {
                log.info("File deleted successfully: {}", path);
            } else {
                log.warn("File was not deleted or does not exist: {}", path);
            }

        } catch (Exception e) {
            log.error("Error while deleting file: {}", path, e);
            throw e;
        }
    }

    public void deleteConsFiles(String path) {

        if (path == null || path.trim().isEmpty()) {
            log.warn("Delete failed: invalid path");
            throw new IllegalArgumentException("Path cannot be null or empty");
        }

        try {
            log.info("Starting file deletion: {}", path);

            boolean deleted = storage.delete(bucketName1, path);

            if (deleted) {
                log.info("File deleted successfully: {}", path);
            } else {
                log.warn("File was not deleted or does not exist: {}", path);
            }

        } catch (Exception e) {
            log.error("Error while deleting file: {}", path, e);
            throw e;
        }
    }

    public String generateUploadUrl(String path, String contentType) {

        if (path == null || path.trim().isEmpty()) {
            log.warn("Generate upload URL failed: invalid path");
            throw new IllegalArgumentException("Path cannot be null or empty");
        }

        try {

            log.info("Generating signed upload URL for path: {}", path);

            BlobInfo blobInfo = BlobInfo
                    .newBuilder(
                            BlobId.of(bucketName, path)
                    )
                    .setContentType(contentType)
                    .build();


            URL url = storage.signUrl(
                    blobInfo,
                    15,
                    TimeUnit.MINUTES,
                    Storage.SignUrlOption.httpMethod(HttpMethod.PUT),
                    Storage.SignUrlOption.withV4Signature()
            );


            log.info("Signed URL generated successfully for: {}", path);

            return url.toString();


        } catch (Exception e) {

            log.error("Error generating signed URL for path: {}", path, e);
            throw new RuntimeException("Unable to generate upload URL", e);
        }
    }

    public String generateUploadUrlForCons(String path, String contentType) {

        if (path == null || path.trim().isEmpty()) {
            log.warn("Generate upload URL failed: invalid path");
            throw new IllegalArgumentException("Path cannot be null or empty");
        }

        try {

            log.info("Generating signed upload URL for path: {}", path);

            BlobInfo blobInfo = BlobInfo
                    .newBuilder(
                            BlobId.of(bucketName1, path)
                    )
                    .setContentType(contentType)
                    .build();


            URL url = storage.signUrl(
                    blobInfo,
                    15,
                    TimeUnit.MINUTES,
                    Storage.SignUrlOption.httpMethod(HttpMethod.PUT),
                    Storage.SignUrlOption.withV4Signature()
            );


            log.info("Signed URL generated successfully for: {}", path);

            return url.toString();


        } catch (Exception e) {

            log.error("Error generating signed URL for path: {}", path, e);
            throw new RuntimeException("Unable to generate upload URL", e);
        }
    }


    public List<ExistingFile> listFilesForConsultation(Long consultationId, Long clientId) {
        String folderPath = clientId + "/consultations/" + consultationId + "/";

        // On liste les blobs avec le préfixe de la consultation
        Page<Blob> blobs = storage.list(bucketName1, Storage.BlobListOption.prefix(folderPath));

        return StreamSupport.stream(blobs.iterateAll().spliterator(), false)
                .filter(blob -> !blob.getName().endsWith("/")) // On ignore le dossier lui-même
                .map(blob -> {
                    // 1. Extraire le nom du fichier
                    String fileName = blob.getName().substring(blob.getName().lastIndexOf('/') + 1);

                    // 2. Générer l'URL signée pour le téléchargement (GET)
                    URL signedUrl = storage.signUrl(
                            blob,
                            15, // Valide 15 minutes
                            TimeUnit.MINUTES,
                            Storage.SignUrlOption.httpMethod(HttpMethod.GET),
                            Storage.SignUrlOption.withV4Signature()
                    );

                    return new ExistingFile(fileName, signedUrl.toString());
                })
                .collect(Collectors.toList());
    }

    public void deleteAllFilesByClient(Long clientId){
        this.deleteLogoByClient(clientId);
        this.deleteConsultationFilesByClient(clientId);
    }

    private void deleteConsultationFilesByClient(Long clientId) {

        if (clientId == null) {
            log.warn("Delete failed: clientId null");
            throw new IllegalArgumentException("Client Id null");
        }

        String prefix = clientId + "/";

        try {
            log.info("Starting deletion of client files with prefix: {}", prefix);

            Iterable<Blob> blobs = storage.list(
                    bucketName1,
                    Storage.BlobListOption.prefix(prefix)
            ).iterateAll();

            int deletedCount = 0;

            for (Blob blob : blobs) {

                String blobName = blob.getName();

                log.debug("Deleting client file: {}", blobName);

                boolean deleted = storage.delete(
                        bucketName1,
                        blobName
                );

                if (deleted) {
                    deletedCount++;
                }
            }

            log.info(
                    "Client files deletion completed. clientId={}, deletedCount={}",
                    clientId,
                    deletedCount
            );

        } catch (Exception e) {
            log.error(
                    "Error while deleting client files for clientId={}",
                    clientId,
                    e
            );
            throw e;
        }
    }

    private void deleteLogoByClient(Long clientId) {

        if (clientId == null) {
            log.warn("Delete failed: clientId null");
            throw new IllegalArgumentException("Client Id null");
        }

        String prefix = clientId + "/";

        try {
            log.info("Starting logo deletion for prefix: {}", prefix);

            Iterable<Blob> blobs = storage.list(
                    bucketName,
                    Storage.BlobListOption.prefix(prefix)
            ).iterateAll();

            int deletedCount = 0;

            for (Blob blob : blobs) {

                String blobName = blob.getName();

                log.debug("Deleting logo: {}", blobName);

                boolean deleted = storage.delete(
                        bucketName,
                        blobName
                );

                if (deleted) {
                    deletedCount++;
                }
            }

            log.info(
                    "Logo deletion completed for clientId={}, deletedCount={}",
                    clientId,
                    deletedCount
            );

        } catch (Exception e) {
            log.error(
                    "Error while deleting logos for clientId={}",
                    clientId,
                    e
            );
            throw e;
        }
    }

    public void deleteFilesByConsultation(Long clientId, Long consultationId) {
        if (clientId == null || consultationId == null) {
            log.warn("Delete failed: clientId or consultationId is null");
            throw new IllegalArgumentException("Client Id or Consultation Id cannot be null");
        }

        // Utilisation d'un format propre
        String prefix = String.format("%d/consultations/%d/", clientId, consultationId);

        try {
            log.info("Starting deletion of client files with prefix: {}", prefix);

            Iterable<Blob> blobs = storage.list(
                    bucketName1,
                    Storage.BlobListOption.prefix(prefix)
            ).iterateAll();

            int deletedCount = 0;

            for (Blob blob : blobs) {
                String blobName = blob.getName();
                log.debug("Deleting file: {}", blobName);

                boolean deleted = storage.delete(bucketName1, blobName);
                if (deleted) {
                    deletedCount++;
                }
            }

            log.info("Consultation files deletion completed. clientId={}, consultationId={}, deletedCount={}",
                    clientId, consultationId, deletedCount);

        } catch (Exception e) {
            log.error("Error while deleting consultation files for clientId={}, consultationId={}",
                    clientId, consultationId, e);
            throw e;
        }
    }
}
