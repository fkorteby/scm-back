package com.simple_cabinet_medical.Backend.controller;

import com.simple_cabinet_medical.Backend.payload.request.AddFormeRequest;
import com.simple_cabinet_medical.Backend.payload.request.AddParametreRequest;
import com.simple_cabinet_medical.Backend.service.MotifService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class MotifController {

    private final MotifService motifService;

    public MotifController(MotifService motifService) {
        this.motifService = motifService;
    }

    @PostMapping("/motif")
    public ResponseEntity<?> createMotif (@RequestBody AddParametreRequest data) {
        return motifService.addMotif(data);
    }

    @GetMapping("/motif")
    public ResponseEntity<?> getAllMotif (@RequestParam Long idUtilisateur) {
        return motifService.getAllMotif(idUtilisateur);
    }

    @GetMapping("/Motif")
    public ResponseEntity<?> getAllMotifByIdUtilisateur (@RequestParam Long idUtilisateur) {
        return motifService.getAllMotifByIdUtilisateur(idUtilisateur);
    }

    @DeleteMapping("/motif/{idMotif}")
    public ResponseEntity<?> deleteMotif (@PathVariable Long idMotif, @RequestParam Long idUtilisateur) {
        return motifService.deleteMotif(idMotif, idUtilisateur);
    }

    @PutMapping("/motif/{idMotif}")
    public ResponseEntity<?> updateMotif (@RequestBody AddParametreRequest data, @PathVariable Long idMotif) {
        return motifService.updateMotif(data, idMotif);
    }
}
