package com.simple_cabinet_medical.Backend.utils;

import com.simple_cabinet_medical.Backend.exception.RecaptchaException;
import com.simple_cabinet_medical.Backend.payload.response.RecaptchaResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class RecaptchaService {

    private static final Logger log = LoggerFactory.getLogger(RecaptchaService.class);

    @Value("${google.recaptcha.secret-key}")
    private String secretKey;

    @Value("${google.recaptcha.verify-url}")
    private String verifyUrl;

    @Value("${google.recaptcha.threshold}")
    private double recaptchaThreshold;

    private final RestTemplate restTemplate;

    public RecaptchaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    public boolean validateToken(String token, String expectedAction) {
        if (token == null || token.isEmpty()) {
            throw new RecaptchaException("Token reCAPTCHA manquant ou invalide.");
        }

        MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<>();
        requestMap.add("secret", secretKey);
        requestMap.add("response", token);

        try {
            RecaptchaResponse response = restTemplate.postForObject(verifyUrl, requestMap, RecaptchaResponse.class);

            if (response == null || !response.isSuccess()) {
                throw new RecaptchaException("Échec de la validation reCAPTCHA.");
            }

            if (expectedAction != null && !expectedAction.equals(response.getAction())) {
                throw new RecaptchaException("L’action reCAPTCHA ne correspond pas.");
            }

            if (response.getScore() < recaptchaThreshold) {
                throw new RecaptchaException("Score reCAPTCHA trop faible : " + response.getScore());
            }

            return true;

        } catch (RecaptchaException e) {
            throw e;
        } catch (Exception e) {
            throw new RecaptchaException("Erreur lors de la vérification du reCAPTCHA.");
        }
    }
}