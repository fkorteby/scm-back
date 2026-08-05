package com.simple_cabinet_medical.Backend.service;

import com.simple_cabinet_medical.Backend.Dto.DiagnosticDTO;
import com.simple_cabinet_medical.Backend.Permission.UtilisateurPermision;
import com.simple_cabinet_medical.Backend.model.Utilisateur;
import com.simple_cabinet_medical.Backend.repository.ConsultationRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsultationService {
    private final ConsultationRepository consultationRepository;
    private final UtilisateurPermision utilisateurPermision;

    public ConsultationService(ConsultationRepository consultationRepository, UtilisateurPermision utilisateurPermision) {
        this.consultationRepository = consultationRepository;
        this.utilisateurPermision = utilisateurPermision;
    }

    public List<DiagnosticDTO> getDiagnostics(Long idClient, String diagnosticMedical) {
        Utilisateur utilisateur = utilisateurPermision.getCurrentUser();
        Long userClientId = utilisateur.getClient().getIdClient();
        if (!userClientId.equals(idClient)) {
            throw new SecurityException("Access denied: You do not have permission to access this client's diagnostics.");
        }
        Pageable pageable = PageRequest.of(0, 10);
        return consultationRepository.getDiagnosticMedical(idClient, diagnosticMedical,pageable);
    }
}
