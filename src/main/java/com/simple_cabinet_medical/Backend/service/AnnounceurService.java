package com.simple_cabinet_medical.Backend.service;

import com.simple_cabinet_medical.Backend.Dto.Pub.AnnonceurRequestDto;
import com.simple_cabinet_medical.Backend.Dto.Pub.AnnonceurResponseDto;
import com.simple_cabinet_medical.Backend.Mapper.Pub.AnnounceurMapper;
import com.simple_cabinet_medical.Backend.model.*;
import com.simple_cabinet_medical.Backend.repository.AnnonceurRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AnnounceurService {
    private final AnnonceurRepository annonceurRepository;
    private final AnnounceurMapper announceurMapper;
    private final UtilisateurService utilisateurService;
    private final EmailService emailService;
    private final OTPSerivce otpSerivce;
    private final PasswordEncoder passwordEncoder;

    public AnnounceurService(AnnonceurRepository annonceurRepository, AnnounceurMapper announceurMapper, UtilisateurService utilisateurService, EmailService emailService, OTPSerivce otpSerivce, PasswordEncoder passwordEncoder) {
        this.annonceurRepository = annonceurRepository;
        this.announceurMapper = announceurMapper;
        this.utilisateurService = utilisateurService;
        this.emailService = emailService;
        this.otpSerivce = otpSerivce;
        this.passwordEncoder = passwordEncoder;
    }

    public AnnonceurResponseDto saveAnnonceur(AnnonceurRequestDto annonceurRequestDto) {
        Annonceur annonceur = announceurMapper.DtoToEntity(annonceurRequestDto);
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail(annonceurRequestDto.getEmailContact());
        utilisateur.setNom(annonceurRequestDto.getNomAnnonceur());
        utilisateur.setPrenom(annonceurRequestDto.getNomAnnonceur());
        utilisateur.setRole(EROLE.ANNONCEUR);
        utilisateur.setNomUtilisateur(utilisateurService.generateUsername(annonceurRequestDto.getNomAnnonceur(), annonceurRequestDto.getPrenomContatct()));
        utilisateur.setMdp(passwordEncoder.encode(utilisateurService.generateRandomPassword(10)));
        utilisateur.setStatus(EStatus.INACTIVE);
        annonceur.setUtilisateur(utilisateur);
        Annonceur annonceur1 = annonceurRepository.save(annonceur);
        emailService.sendOtpEmail(utilisateur.getEmail(), otpSerivce.createEmailOtpForAnnonceur(annonceur.getIdAnnonceur(), "Annonceur Email Verification"));
        return announceurMapper.EntityToDto(annonceur1);
    }

    public AnnonceurResponseDto changeStatus(Long id, String status) {
        Annonceur annonceur = annonceurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Annonceur not found"));
        if (status.isEmpty()) {
            throw new IllegalArgumentException("Status cannot be empty");
        }
        annonceur.setStatus(EStatusAnnonceur.valueOf(status));
        Annonceur savedAnnonceur = annonceurRepository.save(annonceur);
        return announceurMapper.EntityToDto(savedAnnonceur);
    }
}
