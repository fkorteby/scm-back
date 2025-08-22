package com.simple_cabinet_medical.Backend.utils;

import com.simple_cabinet_medical.Backend.model.EROLE;
import com.simple_cabinet_medical.Backend.model.Utilisateur;
import com.simple_cabinet_medical.Backend.repository.UtilisateurRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminInitializer {

    @Bean
    CommandLineRunner initAdmin(UtilisateurRepository utilisateurRepository,
                                PasswordEncoder passwordEncoder) {
        return args -> {
            String adminUsername = "admin123";

            // Vérifier si l’admin existe déjà
            if (utilisateurRepository.findByNomUtilisateur(adminUsername).isEmpty()) {
                Utilisateur admin = new Utilisateur();
                admin.setNom("Admin");
                admin.setPrenom("Super");
                admin.setNomUtilisateur(adminUsername);
                admin.setMdp(passwordEncoder.encode("admin123")); // mot de passe encodé
                admin.setRole(EROLE.ADMIN); // enum EROLE doit contenir ADMIN
                admin.setClient(null); // sans client

                utilisateurRepository.save(admin);
                System.out.println("✅ Utilisateur ADMIN créé avec succès !");
            } else {
                System.out.println("ℹ️ Utilisateur ADMIN déjà existant.");
            }
        };
    }
}
