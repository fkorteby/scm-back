package com.simple_cabinet_medical.Backend.controller;

import com.simple_cabinet_medical.Backend.payload.request.AddMedicamentRequest;
import com.simple_cabinet_medical.Backend.payload.request.AddParacliniqueRequest;
import com.simple_cabinet_medical.Backend.service.ParacliniqueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class ParacliniqueController {

    private final ParacliniqueService paracliniqueService;

    public ParacliniqueController(ParacliniqueService paracliniqueService) {
        this.paracliniqueService = paracliniqueService;
    }

    @PostMapping("/paracliniques")
    public ResponseEntity<?> createParaclinique (@RequestBody AddParacliniqueRequest data) {
        return paracliniqueService.addParaclinique(data);
    }

    @GetMapping("/paracliniques")
    public ResponseEntity<?> getAllParaclinique () {
        return paracliniqueService.getAllParaclinique();
    }

    @GetMapping("/paracliniques/{idUtilisateur}")
    public ResponseEntity<?> getAllParacliniqueByIdUtilisateur (@PathVariable Long idUtilisateur) {
        return paracliniqueService.getAllParacliniqueByIdUtilisateur(idUtilisateur);
    }

    @PutMapping("/paracliniques/{idParaclinique}")
    public ResponseEntity<?> updateMedicament (@RequestBody AddParacliniqueRequest data, @PathVariable Long idParaclinique) {
        return paracliniqueService.updateParaclinique(data, idParaclinique);
    }

    @DeleteMapping("/paracliniques/{idParaclinique}")
    public ResponseEntity<?> deleteMedicament (@PathVariable Long idParaclinique) {
        return paracliniqueService.deleteParaclinique(idParaclinique);
    }
}
