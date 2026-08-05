//package com.simple_cabinet_medical.Backend.controller;
//
//import com.simple_cabinet_medical.Backend.model.ImpressionPub;
//import com.simple_cabinet_medical.Backend.service.ImpressionService;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/api/v1/impressionPub")
//public class ImpressionPubController {
//    private final ImpressionService impressionService;
//
//    public ImpressionPubController(ImpressionService impressionService) {
//        this.impressionService = impressionService;
//    }
//
//    @PostMapping
//    public ResponseEntity<ImpressionPub> addImpressionPub(@RequestParam Long idCompagnePub) {
//        ImpressionPub impressionPub = impressionService.saveImpressionPub(idCompagnePub);
//        return new ResponseEntity<>(impressionPub, HttpStatus.CREATED);
//    }
//}
