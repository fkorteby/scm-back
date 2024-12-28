package com.simple_cabinet_medical.Backend.controller;

import com.simple_cabinet_medical.Backend.payload.request.AddParametreRequest;
import com.simple_cabinet_medical.Backend.service.LocalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class LocalController {

    private final LocalService localService;

    public LocalController(LocalService localService) {
        this.localService = localService;
    }

    @PostMapping("/local")
    public ResponseEntity<?> createLocal () {
        return null;
    }

    @GetMapping("/local")
    public ResponseEntity<?> getAllLocals () {
        return null;
    }

    @PutMapping("/local/{idLocal}")
    public ResponseEntity<?> updateLocal (@PathVariable Long idLocal, @RequestBody AddParametreRequest data) {
        return null;
    }
}
