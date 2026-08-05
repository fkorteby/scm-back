package com.simple_cabinet_medical.Backend.service;

import com.simple_cabinet_medical.Backend.model.Ticket;
import com.simple_cabinet_medical.Backend.model.TicketStatus;
import com.simple_cabinet_medical.Backend.model.Utilisateur;
import com.simple_cabinet_medical.Backend.repository.TicketRepository;
import com.simple_cabinet_medical.Backend.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class TicketSupportService {

    private final TicketRepository ticketRepository;
    private final EmailService emailService;
    private final UtilisateurRepository utilisateurRepository;

    public TicketSupportService(TicketRepository ticketRepository, EmailService emailService, UtilisateurRepository utilisateurRepository) {
        this.ticketRepository = ticketRepository;
        this.emailService = emailService;
        this.utilisateurRepository = utilisateurRepository;
    }

    public Ticket enregistrerEtEnvoyer(Ticket ticket, MultipartFile fichier) {
        // 1. Logique optionnelle pour sauvegarder le fichier localement ou renseigner le statut
        ticket.setStatus(TicketStatus.OUVERT);
        String userEmail = ticket.getEmailDemandeur();
        Utilisateur utilisateur = utilisateurRepository.findUtilisateurByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé pour l'email : " + userEmail));

        ticket.setEtablissementNom(utilisateur.getClient().getNomClient());
        // 2. Sauvegarde en Base
        Ticket ticketSauvegarde = ticketRepository.save(ticket);

        // 3. Envoi simultané aux deux adresses configurées
        try {
            emailService.sendSupportTicketEmail(ticketSauvegarde, fichier);
        } catch (Exception e) {
            // On log l'erreur pour ne pas faire planter la transaction si la BDD est ok mais le serveur mail ralentit
            System.err.println("Le ticket a été sauvegardé mais l'envoi du mail a échoué : " + e.getMessage());
        }

        return ticketSauvegarde;
    }
}