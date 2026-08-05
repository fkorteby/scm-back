package com.simple_cabinet_medical.Backend.service;

import com.simple_cabinet_medical.Backend.model.Ticket;
import com.simple_cabinet_medical.Backend.model.Utilisateur;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public EmailService(JavaMailSender mailSender,
                        TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void sendOtpEmail(String to, String otp) {

        Context context = new Context();
        context.setVariable("otp", otp);

        sendEmailWithLogo(
                to,
                "Votre code de vérification – Simple Cabinet Médical",
                "OTP-email-template",
                context
        );
    }

    public void sendPasswordAndUserName(Utilisateur user,
                                        String tempPassword) {

        Context context = new Context();
        context.setVariable("name", user.getPrenom());
        context.setVariable("username", user.getNomUtilisateur());
        context.setVariable("tempPassword", tempPassword);

        sendEmailWithLogo(
                user.getEmail(),
                "Création de votre compte – Simple Cabinet Médical",
                "user-account-template",
                context
        );
    }

    public void sendUserAccessGrantedEmail(String to,
                                           Utilisateur user,
                                           String tempPassword) {

        Context context = new Context();
        context.setVariable("name", user.getPrenom());
        context.setVariable("username", user.getNomUtilisateur());
        context.setVariable("tempPassword", tempPassword);
        context.setVariable("role", user.getRole().name());
        context.setVariable("cabinetName", user.getClient().getNomClient());

        sendEmailWithLogo(
                to,
                "Accès à la plateforme SCM – Simple Cabinet Médical",
                "user-access-granted",
                context
        );
    }
    public void sendPasswordResetEmail(String email,
                                       Utilisateur utilisateur,
                                       String newPassword) {

        Context context = new Context();
        context.setVariable("name", utilisateur.getPrenom());
        context.setVariable("username", utilisateur.getNomUtilisateur());
        context.setVariable("tempPassword", newPassword);

        sendEmailWithLogo(
                email,
                "Réinitialisation de votre mot de passe – Simple Cabinet Médical",
                "password-reset-template",
                context
        );
    }

    private void sendEmailWithLogo(String to,
                                   String subject,
                                   String template,
                                   Context context) {

        String htmlContent = templateEngine.process(template, context);
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper =
                    new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            ClassPathResource logo =
                    new ClassPathResource("static/email/logo-scm.png");

            helper.addInline("logoSCM", logo);

            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new RuntimeException(
                    "Erreur lors de l'envoi de l'email", e
            );
        }
    }
    public void sendSupportTicketEmail(Ticket ticket, MultipartFile fichier) {
        // Les deux adresses cibles demandées
        String[] targets = {"support.simple.cabinet.medical@gmail.com", "farouk.korteby@gmail.com"};

        Context context = new Context();
        context.setVariable("id", ticket.getId());
        context.setVariable("nomDemandeur", ticket.getNomDemandeur());
        context.setVariable("emailDemandeur", ticket.getEmailDemandeur());
        context.setVariable("type", ticket.getType());
        context.setVariable("module", ticket.getModule());
        context.setVariable("sujet", ticket.getSujet());
        context.setVariable("description", ticket.getDescription());
        context.setVariable("etablissementNom", ticket.getEtablissementNom());
        context.setVariable("medecinTelephone", ticket.getMedecinTelephone());
        context.setVariable("userAgent", ticket.getUserAgent());

        // Génération du contenu HTML via Thymeleaf
        String htmlContent = templateEngine.process("support-ticket-template", context);
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            // 'true' indique qu'on active le mode multipart (requis pour le logo inline + la pièce jointe)
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(targets);
            helper.setSubject("[SUPPORT SCM] " + ticket.getSujet() + " - " + ticket.getEtablissementNom());
            helper.setText(htmlContent, true);

            // Définir l'adresse de l'utilisateur pour le "Répondre à"
            // Quand vous cliquerez sur "Répondre" dans Gmail, l'adresse de l'utilisateur sera automatiquement sélectionnée
            helper.setReplyTo(ticket.getEmailDemandeur());

            // Intégration du logo
            ClassPathResource logo = new ClassPathResource("static/email/logo-scm.png");
            helper.addInline("logoSCM", logo);

            // Ajout de la pièce jointe si elle existe
            if (fichier != null && !fichier.isEmpty()) {
                helper.addAttachment(fichier.getOriginalFilename(), fichier);
            }

            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new RuntimeException("Erreur lors de l'envoi de l'email au support", e);
        }
    }
}
