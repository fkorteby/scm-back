package com.simple_cabinet_medical.Backend.controller;

import com.simple_cabinet_medical.Backend.service.ClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping("/clients")
    public ResponseEntity createClient () {
        return null;
    }

    @DeleteMapping("/clients")
    public ResponseEntity deleteClient () {
        return null;
    }


    @GetMapping("/clients/idClient/{idClient}")
    public ResponseEntity findClient (@PathVariable Long idClient) {
        return null;
    }

    @GetMapping("/clients")
    public ResponseEntity findAllClient () {
        return null;
    }


}
