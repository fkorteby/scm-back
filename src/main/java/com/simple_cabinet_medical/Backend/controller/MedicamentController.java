package com.simple_cabinet_medical.Backend.controller;

import com.simple_cabinet_medical.Backend.payload.request.AddMedicamentRequest;
import com.simple_cabinet_medical.Backend.service.MedicamentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class MedicamentController {

    private final MedicamentService medicamentService;

    public MedicamentController(MedicamentService medicamentService) {
        this.medicamentService = medicamentService;
    }

    @PostMapping("/medicaments")
    public ResponseEntity<?> createMedicament (@RequestBody AddMedicamentRequest data) {
        return medicamentService.addMedicament(data);
    }

    @GetMapping("/medicaments")
    public ResponseEntity<?> getAllMedicamant () {
        return medicamentService.getAllMedicament();
    }

    @GetMapping("/medicaments/{idUtilisateur}")
    public ResponseEntity<?> getAllMedicamentByIdUtilisateur (@PathVariable Long idUtilisateur) {
        return medicamentService.getAllMedicamentByIdUtilisateur(idUtilisateur);
    }

    @PutMapping("/medicaments/{idMedicament}")
    public ResponseEntity<?> updateMedicament (@RequestBody AddMedicamentRequest data, @PathVariable Long idMedicament) {
        return medicamentService.updateMedicament(data, idMedicament);
    }

    @DeleteMapping("/medicaments/{idMedicament}")
    public ResponseEntity<?> deleteMedicament (@PathVariable Long idMedicament) {
        return medicamentService.deleteMedicament(idMedicament);
    }
}
