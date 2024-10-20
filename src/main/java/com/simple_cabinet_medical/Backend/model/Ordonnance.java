package com.simple_cabinet_medical.Backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Ordonnance {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long idOrdonnance;

    @OneToOne (mappedBy = "ordonnance")
    private Consultation consultation;

    @OneToMany ( cascade = CascadeType.ALL  ,fetch = FetchType.LAZY)
    @JoinColumn (name = "ordonnace_id")
    private Set<Traitement> traitements;

    private Long idUtilisateur;

    private Date dateCreation;

    private Date dateModification;
}
