//package com.simple_cabinet_medical.Backend.service;
//
//import com.simple_cabinet_medical.Backend.model.CompagnePublicitaire;
//import com.simple_cabinet_medical.Backend.model.ImpressionPub;
//import com.simple_cabinet_medical.Backend.model.Utilisateur;
//import com.simple_cabinet_medical.Backend.repository.CompagnePublicitaireRepository;
//import com.simple_cabinet_medical.Backend.repository.ImpressionPubRepository;
//import com.simple_cabinet_medical.Backend.repository.UtilisateurRepository;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//
//@Service
//public class ImpressionService {
//    private final ImpressionPubRepository impressionPubRepository;
//    private final CompagnePublicitaireRepository compagnePublicitaireRepository;
//    private final UtilisateurRepository utilisateurRepository;
//
//    public ImpressionService(ImpressionPubRepository impressionPubRepository, CompagnePublicitaireRepository compagnePublicitaireRepository, UtilisateurRepository utilisateurRepository) {
//        this.impressionPubRepository = impressionPubRepository;
//        this.compagnePublicitaireRepository = compagnePublicitaireRepository;
//        this.utilisateurRepository = utilisateurRepository;
//    }
//
//    public ImpressionPub saveImpressionPub(Long idCompagnePub) {
//        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        String username;
//        if (principal instanceof UserDetails) {
//            username = ((UserDetails) principal).getUsername();
//        } else {
//            username = principal.toString();
//        }
//
//        Utilisateur utilisateur = utilisateurRepository.findByNomUtilisateur(username)
//                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
//
//        CompagnePublicitaire campagne = compagnePublicitaireRepository.findById(idCompagnePub)
//                .orElseThrow(() -> new RuntimeException("Campagne non trouvée"));
//        ImpressionPub impressionPub = new ImpressionPub();
//        impressionPub.setUtilisateur(utilisateur);
//        impressionPub.setCampagne(campagne);
//        impressionPub.setDateHeure(LocalDateTime.now());
//
//        compagnePublicitaireRepository.save(campagne);
//        return impressionPubRepository.save(impressionPub);
//    }
//}
