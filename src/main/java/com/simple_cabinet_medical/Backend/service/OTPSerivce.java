package com.simple_cabinet_medical.Backend.service;

import com.simple_cabinet_medical.Backend.Dto.ClientDto;
import com.simple_cabinet_medical.Backend.Dto.OtpVerificationRequest;
import com.simple_cabinet_medical.Backend.Dto.Pub.AnnonceurResponseDto;
import com.simple_cabinet_medical.Backend.Mapper.Client.ClientMapper;
import com.simple_cabinet_medical.Backend.Mapper.Pub.AnnounceurMapper;
import com.simple_cabinet_medical.Backend.model.*;
import com.simple_cabinet_medical.Backend.repository.AnnonceurRepository;
import com.simple_cabinet_medical.Backend.repository.ClientRepository;
import com.simple_cabinet_medical.Backend.repository.OtpRepository;
import com.simple_cabinet_medical.Backend.repository.UtilisateurRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class OTPSerivce {

    private final OtpRepository otpRepository;
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final EmailService emailService;
    private final UtilisateurRepository utilisateurRepository;
    private final UtilisateurService utilisateurService;
    private final AnnonceurRepository annonceurRepository;
    private final AnnounceurMapper announceurMapper;

    public OTPSerivce(OtpRepository otpRepository, ClientRepository clientRepository, ClientMapper clientMapper, EmailService emailService, UtilisateurRepository utilisateurRepository, UtilisateurService utilisateurService, AnnonceurRepository annonceurRepository, AnnounceurMapper announceurMapper) {
        this.otpRepository = otpRepository;
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
        this.emailService = emailService;
        this.utilisateurRepository = utilisateurRepository;
        this.utilisateurService = utilisateurService;
        this.annonceurRepository = annonceurRepository;
        this.announceurMapper = announceurMapper;
    }


    @Transactional
    public String createEmailOtpForClient(String email, String purpose) {

        otpRepository.deleteAllByEmail(email);

        SecureRandom random = new SecureRandom();
        int number = 1000 + random.nextInt(9000);
        char letter1 = (char) ('A' + random.nextInt(26));
        char letter2 = (char) ('A' + random.nextInt(26));

        String otp = number + "" + letter1 + letter2;
        String otpHash = passwordEncoder.encode(otp);

        Otp emailOtp = new Otp();
        emailOtp.setEmail(email);
        emailOtp.setOtpHash(otpHash);
        emailOtp.setAttempts(0);
        emailOtp.setStatus(EOtpStatus.PENDING);
        emailOtp.setCreatedAt(LocalDateTime.now());
        emailOtp.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        emailOtp.setPurpose(purpose);

        otpRepository.save(emailOtp);

        return otp;
    }

    @Transactional
    public String createEmailOtpForAnnonceur(Long annonceurId, String purpose) {

        otpRepository.deleteAllByAnnonceurId(annonceurId);

        SecureRandom random = new SecureRandom();
        int number = 1000 + random.nextInt(9000);
        char letter1 = (char) ('A' + random.nextInt(26));
        char letter2 = (char) ('A' + random.nextInt(26));

        String otp = number + "" + letter1 + letter2;
        String otpHash = passwordEncoder.encode(otp);

        Otp emailOtp = new Otp();
        emailOtp.setAnnonceurId(annonceurId);
        emailOtp.setOtpHash(otpHash);
        emailOtp.setAttempts(0);
        emailOtp.setStatus(EOtpStatus.PENDING);
        emailOtp.setCreatedAt(LocalDateTime.now());
        emailOtp.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        emailOtp.setPurpose(purpose);

        otpRepository.save(emailOtp);

        return otp;
    }

    public Boolean verifyOtpForClient(OtpVerificationRequest request) {

        Otp otpEntity = otpRepository.findOtpByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("OTP not found for this client"));
        if (otpEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
            otpEntity.setStatus(EOtpStatus.EXPIRED);
            otpRepository.save(otpEntity);
            throw new RuntimeException("OTP expired");
        }
        boolean isValid = passwordEncoder.matches(request.getOtp(), otpEntity.getOtpHash());

        if (!isValid) {
            otpEntity.setAttempts(otpEntity.getAttempts() + 1);
            otpRepository.save(otpEntity);
            throw new RuntimeException("Invalid OTP");
        }
        return isValid;
    }

    public AnnonceurResponseDto verifyOtpForAnnounceur(OtpVerificationRequest request) {

        Otp otpEntity = otpRepository.findOtpByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("OTP not found for this Annonceur"));
        if (otpEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
            otpEntity.setStatus(EOtpStatus.EXPIRED);
            otpRepository.save(otpEntity);
            throw new RuntimeException("OTP expired");
        }
        boolean isValid = passwordEncoder.matches(request.getOtp(), otpEntity.getOtpHash());

        if (!isValid) {
            otpEntity.setAttempts(otpEntity.getAttempts() + 1);
            otpRepository.save(otpEntity);
            throw new RuntimeException("Invalid OTP");
        }
        return enableAnnonceur(1L);
    }


    private AnnonceurResponseDto enableAnnonceur(Long annouceurId) {
        Annonceur annonceur = annonceurRepository.findById(annouceurId)
                .orElseThrow(() -> new RuntimeException("Annonceur not found"));
        annonceur.setStatus(EStatusAnnonceur.Actif);
        annonceur.getUtilisateur().setStatus(EStatus.ACTIVE);

        String tempPassword = utilisateurService.generateRandomPassword(10);
        annonceur.getUtilisateur().setMdp(passwordEncoder.encode(tempPassword));
        annonceurRepository.save(annonceur);
        emailService.sendPasswordAndUserName(annonceur.getUtilisateur(), tempPassword);
        return announceurMapper.EntityToDto(annonceur);
    }

}
