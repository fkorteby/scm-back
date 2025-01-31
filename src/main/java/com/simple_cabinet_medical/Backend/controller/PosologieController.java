package com.simple_cabinet_medical.Backend.controller;

import com.simple_cabinet_medical.Backend.payload.request.AddParametreRequest;
import com.simple_cabinet_medical.Backend.service.PosologieService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class PosologieController {

    private final PosologieService posologieService;

    public PosologieController(PosologieService posologieService) {
        this.posologieService = posologieService;
    }

    @GetMapping("/posologies")
    public ResponseEntity<?> getAll (@RequestParam Long idUtilisateur) {
        return posologieService.getAll(idUtilisateur);
    }

    @GetMapping("/posologies/{idUtilisateur}")
    public ResponseEntity<?> getAllByIdUtilisateur (@PathVariable Long idUtilisateur) {
        return posologieService.getAllByIdUtilisateur(idUtilisateur);
    }

    @PostMapping("/posologies")
    public ResponseEntity<?> addPosologie (@RequestBody AddParametreRequest data) {
        return posologieService.add(data);
    }

    @DeleteMapping("/posologies/{idPosologie}")
    public ResponseEntity<?> deletePosologie (@PathVariable Long idPosologie, @RequestParam Long idUtilisateur) {
        return posologieService.delete(idPosologie, idUtilisateur);
    }

    @PutMapping("/posologies/{idPosologie}")
    public ResponseEntity<?> updatePosologie (@RequestBody AddParametreRequest data, @PathVariable Long idPosologie) {
        return posologieService.update(data, idPosologie);
    }
}
