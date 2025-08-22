package com.simple_cabinet_medical.Backend.utils;

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
            return false;
        }

        MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<>();
        requestMap.add("secret", secretKey);
        requestMap.add("response", token);

        try {
            RecaptchaResponse response = restTemplate.postForObject(verifyUrl, requestMap, RecaptchaResponse.class);

            if (response == null) {
                return false;
            }

            if (!response.isSuccess()) {
                return false;
            }
            if (expectedAction != null && !expectedAction.equals(response.getAction())) {
                return false;
            }

            double score = response.getScore();
            if (score >= recaptchaThreshold) {
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            return false;
        }
    }
}