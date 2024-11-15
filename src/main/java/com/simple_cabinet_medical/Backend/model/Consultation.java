package com.simple_cabinet_medical.Backend.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Consultation {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @NotNull
    private Long consultationId;

    @NotNull
    private String motifConsultation;

    @NotNull
    private Date dateConsultation;

    @NotNull
    private String resultatExamenClinique;

    @NotNull
    private String resultatExamenParacliniques;

    @NotNull
    private String diagnosticMedical;

    @OneToOne (fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "ordonnance_id")
    private Ordonnance ordonnance;


    @ManyToOne()
    //@JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    @NotNull
    private Date dateCreation;

}
