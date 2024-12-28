package com.simple_cabinet_medical.Backend.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ResponseHandler {

    public static ResponseEntity generateResponse (String message, HttpStatus status, Object data) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("message",message);
        map.put("data",data);
        map.put("status",status);

        return new ResponseEntity(map,status);
    }
}
