package com.simple_cabinet_medical.Backend.controller;

import com.simple_cabinet_medical.Backend.service.DocumentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("/documents")
    public ResponseEntity<?> createDocument () {
        return null;
    }

    @DeleteMapping("/document/{idDocument}")
    public ResponseEntity<?> deleteDocument (@PathVariable Long idDocument) {
        return null;
    }
}
