package com.simple_cabinet_medical.Backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idClient;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String nomClient;
    @Column(unique = true)
    private String nomClientEnArabe;
    @NotBlank
    @Column(unique = true, nullable = false)
    private String email;
    @Column(unique = true)
    private String contact;
    @Column(unique = true)
    private String adresse;
    private String pays;
    private String wilaya;
    private String ville;
    private String codePostal;
    private String rue;
    @Column(unique = true)
    private String telephone;
    @Column(unique = true)
    private String mobile;
    @Column(unique = true)
    private String site;
    @Column(unique = true)
    private String numeroAgrement;
    @Column(columnDefinition = "TEXT")
    private String description;
    @NotNull
    @Enumerated(EnumType.STRING)
    private EStatus status;

    private String image;

    private String specialite;

    @Enumerated(EnumType.STRING)
    private EmailVerificationStatus emailVerificationStatus;

    @CreatedDate
    protected Date dateCreation;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @RestResource(exported = false)
    private Set<Local> locals = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "idClient")
    @RestResource(exported = false)
    private Set<Utilisateur> utilisateurs;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    @RestResource(exported = false)
    private Set<Patient> patients;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @RestResource(exported = false)
    private Set<Consultation> consultations;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "client_config_id", referencedColumnName = "idClientConfig")
    private ClientConfig clientConfig;
}
