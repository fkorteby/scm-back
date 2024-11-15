package com.simple_cabinet_medical.Backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.simple_cabinet_medical.Backend.service.UserDetailsServiceImpl;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Utilisateur {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long idUtilisateur;

    private String nom;

    private String nomUtilisateur;

    private String mdp;

    private String role;

    private String type;

    private String status;

    @ManyToOne
    @JoinColumn(name="idClient")
    @JsonIgnore
    private Client client;

    @OneToMany
    private Set<Consultation> consultations;

}
