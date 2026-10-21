package com.simple_cabinet_medical.Backend.exception;

public class InvalidRowException extends RuntimeException {

    public InvalidRowException(String message) {
        super(message);
    }

    public InvalidRowException(String message, Throwable cause) {
        super(message, cause);
    }
}