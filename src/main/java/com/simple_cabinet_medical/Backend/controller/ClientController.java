package com.simple_cabinet_medical.Backend.controller;

import com.simple_cabinet_medical.Backend.Dto.ClientDto;
import com.simple_cabinet_medical.Backend.Dto.ClientRegister;
import com.simple_cabinet_medical.Backend.Dto.OtpVerificationRequest;
import com.simple_cabinet_medical.Backend.service.ClientService;
import com.simple_cabinet_medical.Backend.service.OTPSerivce;
import com.simple_cabinet_medical.Backend.utils.RecaptchaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/clients")
public class ClientController {

    private final ClientService clientService;
    private final OTPSerivce otpSerivce;
    private final RecaptchaService recaptchaService;
    public ClientController(ClientService clientService, OTPSerivce otpSerivce, RecaptchaService recaptchaService) {
        this.clientService = clientService;
        this.otpSerivce = otpSerivce;
        this.recaptchaService = recaptchaService;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/status/{id}")
    public ResponseEntity<ClientDto> changeStatus(@PathVariable Long id) {
        ClientDto client = clientService.changeStatus(id);
        return new ResponseEntity<>(client, HttpStatus.CREATED);
    }
    @PostMapping(path = "/register")
    public ResponseEntity<String> registerClient(
            @RequestBody ClientRegister request,
            @RequestParam String recaptchaToken,
            @RequestParam String contentType
    ) {
        recaptchaService.validateToken(recaptchaToken, "register_client");
        String url = clientService.clientRegister(request, contentType);
        return new ResponseEntity<>(url, HttpStatus.CREATED);
    }

    @DeleteMapping("/v2/{clientId}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long clientId){
        clientService.deleteClient(clientId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("changeLogo/{id}")
    @PreAuthorize("@authz.canChangeClientProprite(#id)")
    public ResponseEntity<String> changeLogo(
            @PathVariable Long id,
            @RequestParam String contentType
    ) {
       String url = clientService.changeLogo(id, contentType);
        return new ResponseEntity<>(url,HttpStatus.OK);
    }
    @PostMapping("checkClientOtp")
    public ResponseEntity<Boolean> checkClientOtp(@RequestBody OtpVerificationRequest otpVerificationRequest,@RequestParam String recaptchaToken) {
        recaptchaService.validateToken(recaptchaToken, "check_otp");
        boolean verifiedClient = otpSerivce.verifyOtpForClient(otpVerificationRequest);
        return new ResponseEntity<>(verifiedClient, HttpStatus.OK);
    }
    @PostMapping("generateOtp")
    public ResponseEntity<Void> regenerateOtp(@RequestBody  String email,@RequestParam String recaptchaToken) {
        recaptchaService.validateToken(recaptchaToken, "generate_otp");
        clientService.generateOtp(email);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}