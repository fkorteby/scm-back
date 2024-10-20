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
    private Long idConsultation;

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
    private Ordonnance ordonnance;

    @NotNull
    private Long idUtilisateur;

    @NotNull
    private Date dateCreation;

}
