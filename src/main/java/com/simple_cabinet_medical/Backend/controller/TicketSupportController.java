package com.simple_cabinet_medical.Backend.controller;

import com.simple_cabinet_medical.Backend.model.Ticket;
import com.simple_cabinet_medical.Backend.service.TicketSupportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/support")
public class TicketSupportController {

    private final TicketSupportService ticketService;

    public TicketSupportController(TicketSupportService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping(value = "/ticket", consumes = {"multipart/form-data"})
    public ResponseEntity<Ticket> createTicket(
            @RequestPart("ticket") Ticket ticket,
            @RequestPart(value = "fichier", required = false) MultipartFile fichier) {

        Ticket newTicket = ticketService.enregistrerEtEnvoyer(ticket, fichier);
        return new ResponseEntity<>(newTicket, HttpStatus.CREATED);
    }
}
