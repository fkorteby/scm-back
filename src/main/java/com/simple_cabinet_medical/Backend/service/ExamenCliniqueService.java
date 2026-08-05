package com.simple_cabinet_medical.Backend.service;

import com.simple_cabinet_medical.Backend.Dto.ExamenCliniqueDto;
import com.simple_cabinet_medical.Backend.model.ExamenClinique;
import com.simple_cabinet_medical.Backend.repository.ExamenCliniqueRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static org.antlr.v4.runtime.tree.Trees.getChildren;

@Service
public class ExamenCliniqueService {
    private final ExamenCliniqueRepository examenCliniqueRepository;

    public ExamenCliniqueService(ExamenCliniqueRepository examenCliniqueRepository) {
        this.examenCliniqueRepository = examenCliniqueRepository;
    }

    public List<ExamenCliniqueDto> getExamenCliniqueTree() {
        List<ExamenClinique> allExams = examenCliniqueRepository.findAll();

        Map<Long, ExamenCliniqueDto> dtoMap = allExams.stream()
                .map(ex -> new ExamenCliniqueDto(
                        ex.getIdExamenClinique(),
                        ex.getNomExamenClinique(),
                        ex.getIdParentExamenClinique(),
                        new HashSet<ExamenCliniqueDto>()))  // ← type explicite
                .collect(Collectors.toMap(ExamenCliniqueDto::getIdExamenClinique, ex -> ex));

        List<ExamenCliniqueDto> roots = new ArrayList<>();  // ← ArrayList pour les roots

        for (ExamenCliniqueDto dto : dtoMap.values()) {
            if (dto.getIdParentExamenClinique() == 0) {
                roots.add(dto);
            } else {
                ExamenCliniqueDto parent = dtoMap.get(dto.getIdParentExamenClinique());
                if (parent != null) {
                    parent.getExamenCliniqueDto().add(dto);
                }
            }
        }
        return roots;
    }
}
