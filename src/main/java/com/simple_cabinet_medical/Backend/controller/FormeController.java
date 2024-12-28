package com.simple_cabinet_medical.Backend.controller;

import com.simple_cabinet_medical.Backend.model.Forme;
import com.simple_cabinet_medical.Backend.payload.request.AddFormeRequest;
import com.simple_cabinet_medical.Backend.service.FormeService;
import org.hibernate.annotations.CompositeTypeRegistrations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/")
public class FormeController {

    private final FormeService formeService;

    public FormeController(FormeService formeService) {
        this.formeService = formeService;
    }

    @PostMapping("/formes")
    public ResponseEntity<?> createForme (@RequestBody AddFormeRequest data) {
        return formeService.addForme(data);
    }

    @GetMapping("/formes")
    public ResponseEntity<?> getAllForme () {
        return formeService.getAllForme();
    }

    @GetMapping("/formes/{idUtilisateur}")
    public ResponseEntity<?> getAllFormeByIdUtilisateur (@PathVariable Long idUtilisateur) {
        return formeService.findAllFormeByIdUtilisateur(idUtilisateur);
    }

    @DeleteMapping("/formes/{idForme}")
    public ResponseEntity<?> deleteForme (@PathVariable Long idForme) {
        return formeService.deleteForme(idForme);
    }

    @PutMapping("/formes/{idForme}")
    public ResponseEntity<?> updateForme (@RequestBody AddFormeRequest data, @PathVariable Long idForme) {
        return formeService.updateForme(data, idForme);
    }
}