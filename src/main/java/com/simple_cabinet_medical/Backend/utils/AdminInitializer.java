package com.simple_cabinet_medical.Backend.utils;

import com.simple_cabinet_medical.Backend.model.Client;
import com.simple_cabinet_medical.Backend.model.EROLE;
import com.simple_cabinet_medical.Backend.model.EStatus;
import com.simple_cabinet_medical.Backend.model.Utilisateur;
import com.simple_cabinet_medical.Backend.repository.UtilisateurRepository;
import jakarta.persistence.EntityManager;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminInitializer {

    @Bean
    CommandLineRunner initAdmin(UtilisateurRepository utilisateurRepository,
                                PasswordEncoder passwordEncoder, EntityManager entityManager) {
        return args -> {
            String adminUsername = "admin123";
            Long clientId = 1L;

            // Vérifier si l’admin existe déjà
            if (utilisateurRepository.findByNomUtilisateur(adminUsername).isEmpty()) {
                Utilisateur admin = new Utilisateur();
                admin.setNom("Admin");
                admin.setPrenom("Super");
                admin.setStatus(EStatus.ACTIVE);
                admin.setNomUtilisateur(adminUsername);
                admin.setEmail("admin@gmail.com");
                admin.setMdp(passwordEncoder.encode("admin123")); // mot de passe encodé
                admin.setRole(EROLE.ADMIN); // enum EROLE doit contenir ADMIN

                var client = entityManager.find(Client.class, clientId);
                admin.setClient(client);

                utilisateurRepository.save(admin);
                System.out.println("Utilisateur ADMIN créé avec succès !");
            } else {
                System.out.println("Utilisateur ADMIN déjà existant.");
            }
        };
    }
}
