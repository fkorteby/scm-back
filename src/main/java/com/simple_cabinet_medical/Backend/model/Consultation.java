package com.simple_cabinet_medical.Backend.model;


import jakarta.persistence.*;
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
    private Long id;

    private String motifConsultation;

    private Date dateConsultation;

    private String resultatExamenClinique;

    private String resultatExamenParacliniques;

    private String diagnosticMedical;

    @OneToOne (fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    private Ordonnance ordonnance;

}
