package com.simple_cabinet_medical.Backend.controller;

import com.simple_cabinet_medical.Backend.Dto.OtpVerificationRequest;
import com.simple_cabinet_medical.Backend.Dto.Pub.AnnonceurRequestDto;
import com.simple_cabinet_medical.Backend.Dto.Pub.AnnonceurResponseDto;
import com.simple_cabinet_medical.Backend.service.AnnounceurService;
import com.simple_cabinet_medical.Backend.service.OTPSerivce;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/annonceurs")
public class AnounceurController {

    private final AnnounceurService announceurService;
    private final OTPSerivce otpSerivce;

    public AnounceurController(AnnounceurService announceurService, OTPSerivce otpSerivce) {
        this.announceurService = announceurService;
        this.otpSerivce = otpSerivce;
    }

    @PostMapping(path = "/register")
    public ResponseEntity<AnnonceurResponseDto> registerClient(@RequestBody AnnonceurRequestDto request) {
        AnnonceurResponseDto annonceurResponseDto = announceurService.saveAnnonceur(request);
        return new ResponseEntity<>(annonceurResponseDto, HttpStatus.CREATED);
    }

    @PostMapping("checkOtp")
    public ResponseEntity<AnnonceurResponseDto> checkClientOtp(@RequestBody OtpVerificationRequest otpVerificationRequest) {
        AnnonceurResponseDto annonceurResponseDto = otpSerivce.verifyOtpForAnnounceur(otpVerificationRequest);
        return new ResponseEntity<>(annonceurResponseDto, HttpStatus.OK);
    }

    @PatchMapping("status/{id}")
    public ResponseEntity<AnnonceurResponseDto> changeStatus(@PathVariable Long id, @RequestParam String status) {
        AnnonceurResponseDto annonceurResponseDto = announceurService.changeStatus(id, status);
        return new ResponseEntity<>(annonceurResponseDto, HttpStatus.OK);
    }
}
