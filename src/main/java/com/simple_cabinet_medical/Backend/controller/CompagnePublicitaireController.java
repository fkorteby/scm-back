package com.simple_cabinet_medical.Backend.controller;

import com.simple_cabinet_medical.Backend.Dto.Pub.CompagnePubRequestDto;
import com.simple_cabinet_medical.Backend.service.CompagnePublicitaireServcie;
import com.simple_cabinet_medical.Backend.service.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/compagnePubs")
public class CompagnePublicitaireController {

    private final CompagnePublicitaireServcie compagnePublicitaireServcie;
    private final FileStorageService fileStorageService;

    public CompagnePublicitaireController(CompagnePublicitaireServcie compagnePublicitaireServcie, FileStorageService fileStorageService) {
        this.compagnePublicitaireServcie = compagnePublicitaireServcie;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> createCompagnePublicitaire(@RequestPart CompagnePubRequestDto requestDto,
                                                           @RequestPart(value = "visuel", required = false) MultipartFile visuel ) {
        compagnePublicitaireServcie.create(requestDto,visuel);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PatchMapping("{id}")
    public ResponseEntity<Void> updateCompagnePublicitaire(@PathVariable Long id, @RequestBody CompagnePubRequestDto requestDto) {
        compagnePublicitaireServcie.update(requestDto, id);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    @PatchMapping("{id}/visuel")
    public ResponseEntity<Void> updateVisuelCompagnePublicitaire(@PathVariable Long id,
                                                                 @RequestPart("visuel") MultipartFile visuel) {
        compagnePublicitaireServcie.changeVisuelOfCompagnePub(visuel,id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/visuel")
    public ResponseEntity<Resource> getVisuelByPath(@RequestParam("path") String path, HttpServletRequest request) {
        Resource resource = fileStorageService.loadFileAsResource(path);
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
        }
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
