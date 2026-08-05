package com.simple_cabinet_medical.Backend.controller;

import com.simple_cabinet_medical.Backend.Dto.ExamenCliniqueDto;
import com.simple_cabinet_medical.Backend.service.ExamenCliniqueService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/examenCliniques")
@PreAuthorize("hasAnyAuthority('ADMIN', 'MEDECIN', 'REMPLACANT', 'SECRETAIRE','MEDECIN_PRINCIPAL')")
public class ExamenCliniqueController {
    private final ExamenCliniqueService examenCliniqueService;

    public ExamenCliniqueController(ExamenCliniqueService examenCliniqueService) {
        this.examenCliniqueService = examenCliniqueService;
    }

    @GetMapping("/tree")
    public List<ExamenCliniqueDto> getTree() {
        return examenCliniqueService.getExamenCliniqueTree();
    }
}
