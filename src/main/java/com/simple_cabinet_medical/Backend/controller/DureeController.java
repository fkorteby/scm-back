package com.simple_cabinet_medical.Backend.controller;

import com.simple_cabinet_medical.Backend.payload.request.AddFormeRequest;
import com.simple_cabinet_medical.Backend.payload.request.AddParametreRequest;
import com.simple_cabinet_medical.Backend.service.DureeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class DureeController {

    private final DureeService dureeService;

    public DureeController(DureeService dureeService) {
        this.dureeService = dureeService;
    }

    @GetMapping("/duree")
    public ResponseEntity<?> findAllDuree (@RequestParam Long idUtilisateur) {
        return dureeService.getAll(idUtilisateur);
    }

    @GetMapping("/duree/{idUtilisateur}")
    public ResponseEntity<?> findAllDureeByIdUtilisateur (@PathVariable Long idUtilisateur) {
        return dureeService.getAllByIdUtilisateur(idUtilisateur);
    }

    @PostMapping("/duree")
    public ResponseEntity<?> createDuree (@RequestBody AddParametreRequest data) {

        return dureeService.add(data);
    }

    @DeleteMapping("/duree/{idDuree}")
    public ResponseEntity<?> deleteDuree (@PathVariable Long idDuree, @RequestParam Long idUtilisateur) {
        return dureeService.delete(idDuree, idUtilisateur);
    }

    @PutMapping("/duree/{idDuree}")
    public ResponseEntity<?> updateDuree (@RequestBody AddParametreRequest data,@PathVariable Long idDuree) {
        return dureeService.update(data, idDuree);
    }

}
