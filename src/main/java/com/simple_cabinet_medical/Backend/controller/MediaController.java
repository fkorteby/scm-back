package com.simple_cabinet_medical.Backend.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/pub/media")
public class MediaController {

    // Chemin absolu ou relatif vers ton dossier racine d'upload
    private final Path rootLocation = Paths.get("upload").toAbsolutePath().normalize();

    @GetMapping("/view")
    public ResponseEntity<Resource> getMedia(@RequestParam("path") String mediaPath) {
        try {
            // Nettoie le chemin pour enlever un éventuel "uploads/" en trop au début
            String cleanPath = mediaPath.replace("uploads/", "");

            // Reconstruit le chemin physique complet sur ton PC
            Path file = rootLocation.resolve(cleanPath).normalize();
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                // Détecte automatiquement si c'est une image (png, jpg) ou une vidéo (mp4)
                String contentType = Files.probeContentType(file);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}