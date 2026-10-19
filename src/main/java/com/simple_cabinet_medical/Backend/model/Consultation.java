package com.simple_cabinet_medical.Backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Consultation extends BasedObject implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "consultation_seq")
    @SequenceGenerator(
            name = "consultation_seq",
            sequenceName = "consultation_id_seq",
            allocationSize = 1
    )
    private Long idConsultation;

    private LocalDate dateConsultation;

    private EStatusConsultation statusConsultation;

    @Column(columnDefinition = "TEXT")
    private String resultatExamenClinique;

    @Column(columnDefinition = "TEXT")
    private String resultatExamenParacliniques;

    @Column(columnDefinition = "TEXT")
    private String diagnosticMedical;

    @Column(columnDefinition = "TEXT")
    private String traitement;

    @Column(columnDefinition = "TEXT")
    private String catEvolution;

    @Column(columnDefinition = "TEXT")
    private String motifConsultation;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "patient_id", referencedColumnName = "idPatient")
    private Patient patient;

    @OneToOne(mappedBy = "consultation")
    private RendezVous rendezVous;

    @OneToMany(mappedBy = "consultation", cascade = CascadeType.ALL)
    private List<Traitement> traitements;
}