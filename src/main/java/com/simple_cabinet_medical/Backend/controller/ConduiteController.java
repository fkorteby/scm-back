package com.simple_cabinet_medical.Backend.controller;

import com.simple_cabinet_medical.Backend.payload.request.AddParametreRequest;
import com.simple_cabinet_medical.Backend.service.ConduiteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class ConduiteController {

    private final ConduiteService conduiteService;

    public ConduiteController(ConduiteService conduiteService) {
        this.conduiteService = conduiteService;
    }

    @PostMapping("/conduites")
    public ResponseEntity<?> createConduite (@RequestBody AddParametreRequest data) {
        return conduiteService.add(data);
    }

    @GetMapping("/conduites")
    public ResponseEntity<?> getAllConduite (@RequestParam Long idUtilisateur) {
        return conduiteService.getAll(idUtilisateur);
    }

    @GetMapping("/conduites/utilisateur/{idUtilisateur}")
    public ResponseEntity<?> getAllConduiteByIdUtilisateur (@PathVariable Long idUtilisateur) {
        return conduiteService.getAllByIdUtilisateur(idUtilisateur);
    }

    @DeleteMapping("/conduites/{idConduite}")
    public ResponseEntity<?> deleteConduite (@PathVariable Long idConduite, @RequestParam Long idUtilisateur) {
        return conduiteService.delete(idConduite, idUtilisateur);
    }

    @PutMapping("/conduites/{idConduite}")
    public ResponseEntity<?> updateConduite (@RequestBody AddParametreRequest data, @PathVariable Long idConduite) {
        return conduiteService.update(data, idConduite);
    }

}
