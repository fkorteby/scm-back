package com.simple_cabinet_medical.Backend.controller;

import com.simple_cabinet_medical.Backend.payload.request.AddParametreRequest;
import com.simple_cabinet_medical.Backend.service.OptionParacliniqueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class OptionParacliniqueController {

    private final OptionParacliniqueService optionParacliniqueService;

    public OptionParacliniqueController(OptionParacliniqueService optionParacliniqueService) {
        this.optionParacliniqueService = optionParacliniqueService;
    }

    @GetMapping("options-paraclinique")
    public ResponseEntity<?> getAll () {
        return optionParacliniqueService.getAll();
    }

    @GetMapping("options-paraclinique/utilisateur/{idUtilisateur}")
    public ResponseEntity<?> getAllByIdUtilisateur (@PathVariable Long idUtilisateur) {
        return optionParacliniqueService.getAllByIdUtilisateur(idUtilisateur);
    }

    @PostMapping("options-paraclinique")
    public ResponseEntity<?> addOption (@RequestBody AddParametreRequest data) {
        return optionParacliniqueService.add(data);
    }

    @DeleteMapping("options-paraclinique/{idOption}")
    public ResponseEntity<?> deleteOption (@PathVariable Long idOption) {
        return optionParacliniqueService.delete(idOption);
    }

    @PutMapping("options-paraclinique/{idOption}")
    public ResponseEntity<?> updateOption (@RequestBody AddParametreRequest data,@PathVariable Long idOption) {
        return optionParacliniqueService.update(data,idOption);
    }
}
