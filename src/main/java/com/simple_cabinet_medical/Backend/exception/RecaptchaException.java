package com.simple_cabinet_medical.Backend.exception;

public class RecaptchaException extends RuntimeException {
    public RecaptchaException(String message) {
        super(message);
    }
}
