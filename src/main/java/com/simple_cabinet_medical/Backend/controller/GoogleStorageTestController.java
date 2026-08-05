package com.simple_cabinet_medical.Backend.controller;

import com.simple_cabinet_medical.Backend.service.GoogleStorageService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test/gcs")
public class GoogleStorageTestController {


    private final GoogleStorageService googleStorageService;


    public GoogleStorageTestController(
            GoogleStorageService googleStorageService
    ) {
        this.googleStorageService = googleStorageService;
    }


    @PostMapping("/upload-url")
    public String generateUploadUrl(
            @RequestParam Long clientId
    ) {

        // Chemin choisi par le backend
        String path = "clients/"
                + clientId
                + "/logo.png";


        // Type du fichier attendu
        String contentType = "image/png";


        return googleStorageService.generateUploadUrl(
                path,
                contentType
        );
    }
}