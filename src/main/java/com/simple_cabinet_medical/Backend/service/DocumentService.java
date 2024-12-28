package com.simple_cabinet_medical.Backend.service;

import com.simple_cabinet_medical.Backend.repository.DocumentRepository;
import org.springframework.stereotype.Service;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public void addDocument () {}

    public void deleteDocument () {}

    public void updateDocument () {}
}
