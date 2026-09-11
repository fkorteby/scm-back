package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.Dto.Dash.ConsultationsParMoisDTO;
import com.simple_cabinet_medical.Backend.Dto.DiagnosticDTO;
import com.simple_cabinet_medical.Backend.Dto.RapportSummaryPatientsDTO;
import com.simple_cabinet_medical.Backend.Dto.RapportSummaryProjection;
import com.simple_cabinet_medical.Backend.Projection.ConsultationsProjection;
import com.simple_cabinet_medical.Backend.model.Consultation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RepositoryRestResource(excerptProjection = ConsultationsProjection.class)
@PostFilter("hasPermission(filterObject,'READ')")
public interface ConsultationRepository extends JpaRepository<Consultation, Long> {

    @PreAuthorize("((#consultation.idConsultation == null or #consultation.idConsultation == 0 )? hasPermission(#consultation,'WRITE') : hasPermission(#consultation,'UPDATE'))")
    Consultation save(Consultation consultation);

    @RestResource(path = "by-client", rel = "by-client")
    Page<Consultation> findByClientCreatorIdOrderByDateConsultationDesc(@Param("clientId") Long clientId, Pageable pageable);

    @RestResource(path = "by-date", rel = "by-date")
    List<Consultation> findAllByClientCreatorIdAndDateConsultationOrderByDateConsultationDesc(@Param("clientId") Long clientId,
                                                                   @Param("date") LocalDate dateConsultation,
                                                                   Pageable pageable);

    @PreAuthorize("hasPermission(#id,'Consultation','DELETE')")
    void deleteById(Long id);

    List<Consultation> findAllByClientCreatorIdOrderByDateConsultationDesc(Long clientCreatorId);

    List<Consultation> findAllByClientCreatorIdAndDateConsultationOrderByDateConsultationDesc(Long clientCreatorId, LocalDate dateConsultation);

    @Query("""
            SELECT new com.simple_cabinet_medical.Backend.Dto.Dash.ConsultationsParMoisDTO(
                YEAR(c.dateConsultation),
                MONTH(c.dateConsultation),
                COUNT(c.idConsultation)
            )
            FROM Consultation c
            WHERE c.clientCreatorId = :idClient
            GROUP BY YEAR(c.dateConsultation), MONTH(c.dateConsultation)
            ORDER BY YEAR(c.dateConsultation), MONTH(c.dateConsultation) DESC 
            """)
    List<ConsultationsParMoisDTO> countConsultationsByMonthAndYear(@Param("idClient") Long idClient);

    @RestResource(path = "Allby-date", rel = "Allby-date")
    List<Consultation> findAllByDateConsultation(@Param("date") LocalDate dateConsultation, Pageable pageable);

    @RestResource(path = "byPatient", rel = "byPatient")
    @Query("""
    SELECT c
    FROM Consultation c
    WHERE c.clientCreatorId = :idClient
      AND (
            LOWER(c.patient.nom) LIKE LOWER(CONCAT('%', :patient, '%'))
         OR LOWER(c.patient.prenom) LIKE LOWER(CONCAT('%', :patient, '%'))
      )
    ORDER BY c.dateConsultation DESC
""")
    Page<Consultation> searchByPatient(
            @Param("idClient") Long idClient,
            @Param("patient") String patient,
            Pageable pageable);


    @Query(value = """
            SELECT
                DATE(date_consultation) AS consultation_date,
                COUNT(id_consultation) AS total_consultations
            FROM consultation
            WHERE client_creator_id = :clientId
                AND date_consultation >= :startDate
            GROUP BY DATE(date_consultation)
            ORDER BY consultation_date DESC 
            """, nativeQuery = true)
    List<Object[]> getConsultationsParJour(
            @Param("clientId") Long clientId,
            @Param("startDate") LocalDate startDate
    );

    Optional<Long> countByClientCreatorId(Long clientCreatorId);

    @RestResource(exported = false)
    @Query("""
    SELECT DISTINCT new com.simple_cabinet_medical.Backend.Dto.DiagnosticDTO(c.diagnosticMedical)
    FROM Consultation c
    WHERE c.clientCreatorId = :idClient
      AND TRANSLATE(
            LOWER(REPLACE(c.diagnosticMedical, ' ', '')),
            'àâäçéèêëîïôöùûüÿ',
            'aaaceeeeii oouuuy'
          )
        LIKE CONCAT('%',
            TRANSLATE(
                LOWER(REPLACE(:diagnosticMedical, ' ', '')),
                'àâäçéèêëîïôöùûüÿ',
                'aaaceeeeii oouuuy'
            ),
        '%')
""")
    List<DiagnosticDTO> getDiagnosticMedical(
            @Param("idClient") Long idClient,
            @Param("diagnosticMedical") String diagnosticMedical,
            Pageable pageable);

    @Query(value = """
            SELECT diagnostic_medical, COUNT(id_consultation)
            FROM consultation as c
                    where c.client_creator_id = :idClient
            GROUP BY diagnostic_medical
            LIMIT 10
            """, nativeQuery = true)
    List<Object> getTop10DiagnosticMedical(Long idClient);

    @Query(value = """
            SELECT
                EXTRACT(YEAR FROM date_consultation) AS year,
                EXTRACT(MONTH FROM date_consultation) AS month,
                COUNT(id_consultation) AS total_consultations
            FROM consultation
            WHERE client_creator_id = :clientId
            GROUP BY EXTRACT(YEAR FROM date_consultation), EXTRACT(MONTH FROM date_consultation)
            ORDER BY year, month DESC 
            """, nativeQuery = true)
    List<Object[]> getConsultationsParMois(@Param("clientId") Long clientId);

    // =====================================================
    // REPORTS — Rapport consultations filtré
    // =====================================================

    @RestResource(path = "rapport", rel = "rapport")
    @Query(value = """
        SELECT * FROM consultation c
        WHERE c.client_creator_id = :clientId
          AND (:dateDebut  IS NULL OR c.date_consultation >= CAST(:dateDebut  AS date))
          AND (:dateFin    IS NULL OR c.date_consultation <= CAST(:dateFin    AS date))
          AND (:diagnostic     IS NULL OR lower(c.diagnostic_medical::text)      LIKE lower('%' || :diagnostic     || '%'))
          AND (:motif          IS NULL OR lower(c.motif_consultation::text)       LIKE lower('%' || :motif          || '%'))
          AND (:examenClinique IS NULL OR lower(c.resultat_examen_clinique::text) LIKE lower('%' || :examenClinique || '%'))
          AND (:catEvolution   IS NULL OR lower(c.cat_evolution::text)            LIKE lower('%' || :catEvolution   || '%'))
        ORDER BY c.date_consultation DESC
        """,
            countQuery = """
        SELECT COUNT(*) FROM consultation c
        WHERE c.client_creator_id = :clientId
          AND (:dateDebut  IS NULL OR c.date_consultation >= CAST(:dateDebut  AS date))
          AND (:dateFin    IS NULL OR c.date_consultation <= CAST(:dateFin    AS date))
          AND (:diagnostic     IS NULL OR lower(c.diagnostic_medical::text)      LIKE lower('%' || :diagnostic     || '%'))
          AND (:motif          IS NULL OR lower(c.motif_consultation::text)       LIKE lower('%' || :motif          || '%'))
          AND (:examenClinique IS NULL OR lower(c.resultat_examen_clinique::text) LIKE lower('%' || :examenClinique || '%'))
          AND (:catEvolution   IS NULL OR lower(c.cat_evolution::text)            LIKE lower('%' || :catEvolution   || '%'))
        """,
            nativeQuery = true)
    Page<Consultation> findRapportByClient(
            @Param("clientId")       Long clientId,
            @Param("dateDebut")      String dateDebut,
            @Param("dateFin")        String dateFin,
            @Param("diagnostic")     String diagnostic,
            @Param("motif")          String motif,
            @Param("examenClinique") String examenClinique,
            @Param("catEvolution")   String catEvolution,
            Pageable pageable
    );

    @RestResource(path = "rapportAll", rel = "rapportAll")
    @Query(value = """
        SELECT * FROM consultation c
        WHERE (:dateDebut  IS NULL OR c.date_consultation >= CAST(:dateDebut  AS date))
          AND (:dateFin    IS NULL OR c.date_consultation <= CAST(:dateFin    AS date))
          AND (:diagnostic     IS NULL OR lower(c.diagnostic_medical::text)      LIKE lower('%' || :diagnostic     || '%'))
          AND (:motif          IS NULL OR lower(c.motif_consultation::text)       LIKE lower('%' || :motif          || '%'))
          AND (:examenClinique IS NULL OR lower(c.resultat_examen_clinique::text) LIKE lower('%' || :examenClinique || '%'))
          AND (:catEvolution   IS NULL OR lower(c.cat_evolution::text)            LIKE lower('%' || :catEvolution   || '%'))
        ORDER BY c.date_consultation DESC
        """,
            countQuery = """
        SELECT COUNT(*) FROM consultation c
        WHERE (:dateDebut  IS NULL OR c.date_consultation >= CAST(:dateDebut  AS date))
          AND (:dateFin    IS NULL OR c.date_consultation <= CAST(:dateFin    AS date))
          AND (:diagnostic     IS NULL OR lower(c.diagnostic_medical::text)      LIKE lower('%' || :diagnostic     || '%'))
          AND (:motif          IS NULL OR lower(c.motif_consultation::text)       LIKE lower('%' || :motif          || '%'))
          AND (:examenClinique IS NULL OR lower(c.resultat_examen_clinique::text) LIKE lower('%' || :examenClinique || '%'))
          AND (:catEvolution   IS NULL OR lower(c.cat_evolution::text)            LIKE lower('%' || :catEvolution   || '%'))
        """,
            nativeQuery = true)
    Page<Consultation> findRapportAll(
            @Param("dateDebut")      String dateDebut,
            @Param("dateFin")        String dateFin,
            @Param("diagnostic")     String diagnostic,
            @Param("motif")          String motif,
            @Param("examenClinique") String examenClinique,
            @Param("catEvolution")   String catEvolution,
            Pageable pageable
    );


    @RestResource(path = "reportForExport", rel = "reportForExport")
    @Query(value = """
        SELECT * FROM consultation c
        WHERE c.client_creator_id = :clientId
          AND (:dateDebut  IS NULL OR c.date_consultation >= CAST(:dateDebut  AS date))
          AND (:dateFin    IS NULL OR c.date_consultation <= CAST(:dateFin    AS date))
          AND (:diagnostic     IS NULL OR lower(c.diagnostic_medical::text)      LIKE lower('%' || :diagnostic     || '%'))
          AND (:motif          IS NULL OR lower(c.motif_consultation::text)       LIKE lower('%' || :motif          || '%'))
          AND (:examenClinique IS NULL OR lower(c.resultat_examen_clinique::text) LIKE lower('%' || :examenClinique || '%'))
          AND (:catEvolution   IS NULL OR lower(c.cat_evolution::text)            LIKE lower('%' || :catEvolution   || '%'))
        ORDER BY c.date_consultation DESC
        """,
            countQuery = """
        SELECT COUNT(*) FROM consultation c
        WHERE c.client_creator_id = :clientId
          AND (:dateDebut  IS NULL OR c.date_consultation >= CAST(:dateDebut  AS date))
          AND (:dateFin    IS NULL OR c.date_consultation <= CAST(:dateFin    AS date))
          AND (:diagnostic     IS NULL OR lower(c.diagnostic_medical::text)      LIKE lower('%' || :diagnostic     || '%'))
          AND (:motif          IS NULL OR lower(c.motif_consultation::text)       LIKE lower('%' || :motif          || '%'))
          AND (:examenClinique IS NULL OR lower(c.resultat_examen_clinique::text) LIKE lower('%' || :examenClinique || '%'))
          AND (:catEvolution   IS NULL OR lower(c.cat_evolution::text)            LIKE lower('%' || :catEvolution   || '%'))
        ORDER BY c.date_consultation DESC
        """,
            nativeQuery = true)
    List<Consultation> findRapportByClient(
            @Param("clientId")       Long clientId,
            @Param("dateDebut")      String dateDebut,
            @Param("dateFin")        String dateFin,
            @Param("diagnostic")     String diagnostic,
            @Param("motif")          String motif,
            @Param("examenClinique") String examenClinique,
            @Param("catEvolution")   String catEvolution
    );

    @RestResource(path = "reportAllForExport", rel = "reportAllForExport")
    @Query(value = """
        SELECT * FROM consultation c
        WHERE (:dateDebut  IS NULL OR c.date_consultation >= CAST(:dateDebut  AS date))
          AND (:dateFin    IS NULL OR c.date_consultation <= CAST(:dateFin    AS date))
          AND (:diagnostic     IS NULL OR lower(c.diagnostic_medical::text)      LIKE lower('%' || :diagnostic     || '%'))
          AND (:motif          IS NULL OR lower(c.motif_consultation::text)       LIKE lower('%' || :motif          || '%'))
          AND (:examenClinique IS NULL OR lower(c.resultat_examen_clinique::text) LIKE lower('%' || :examenClinique || '%'))
          AND (:catEvolution   IS NULL OR lower(c.cat_evolution::text)            LIKE lower('%' || :catEvolution   || '%'))
        ORDER BY c.date_consultation DESC
        """,
            countQuery = """
        SELECT COUNT(*) FROM consultation c
        WHERE (:dateDebut  IS NULL OR c.date_consultation >= CAST(:dateDebut  AS date))
          AND (:dateFin    IS NULL OR c.date_consultation <= CAST(:dateFin    AS date))
          AND (:diagnostic     IS NULL OR lower(c.diagnostic_medical::text)      LIKE lower('%' || :diagnostic     || '%'))
          AND (:motif          IS NULL OR lower(c.motif_consultation::text)       LIKE lower('%' || :motif          || '%'))
          AND (:examenClinique IS NULL OR lower(c.resultat_examen_clinique::text) LIKE lower('%' || :examenClinique || '%'))
          AND (:catEvolution   IS NULL OR lower(c.cat_evolution::text)            LIKE lower('%' || :catEvolution   || '%'))
        ORDER BY c.date_consultation DESC
        """,
            nativeQuery = true)
    List<Consultation> findRapportAll(
            @Param("dateDebut")      String dateDebut,
            @Param("dateFin")        String dateFin,
            @Param("diagnostic")     String diagnostic,
            @Param("motif")          String motif,
            @Param("examenClinique") String examenClinique,
            @Param("catEvolution")   String catEvolution
    );


    // =====================================================
    // GRAPHIQUES — Queries filtrées par période
    // =====================================================

    /**
     * Consultations par jour entre deux dates.
     * Appelé par: GET /statistique/consultations/jour-periode/{clientId}
     */
    @Query(value = """
            SELECT
                DATE(c.date_consultation)  AS jour,
                COUNT(c.id_consultation)   AS total
            FROM consultation c
            WHERE c.client_creator_id = :clientId
              AND c.date_consultation  >= :dateDebut
              AND c.date_consultation  <= :dateFin
            GROUP BY DATE(c.date_consultation)
            ORDER BY jour
            """, nativeQuery = true)
    List<Object[]> countConsultationsParJourPeriode(
            @Param("clientId")  Long clientId,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin")   LocalDate dateFin
    );

    /**
     * Consultations par mois entre deux dates.
     * Appelé par: GET /statistique/consultations/mois-periode/{clientId}
     */
    @Query(value = """
            SELECT
                EXTRACT(YEAR  FROM c.date_consultation) AS annee,
                EXTRACT(MONTH FROM c.date_consultation) AS mois,
                COUNT(c.id_consultation)                AS total
            FROM consultation c
            WHERE c.client_creator_id = :clientId
              AND c.date_consultation  >= :dateDebut
              AND c.date_consultation  <= :dateFin
            GROUP BY annee, mois
            ORDER BY annee, mois
            """, nativeQuery = true)
    List<Object[]> countConsultationsParMoisPeriode(
            @Param("clientId")  Long clientId,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin")   LocalDate dateFin
    );

    /**
     * Top 10 diagnostics entre deux dates.
     * Appelé par: GET /statistique/diagnostic-periode/{clientId}
     */
    @Query(value = """
            SELECT
                c.diagnostic_medical,
                COUNT(c.id_consultation) AS total
            FROM consultation c
            WHERE c.client_creator_id = :clientId
              AND c.date_consultation  >= :dateDebut
              AND c.date_consultation  <= :dateFin
              AND c.diagnostic_medical IS NOT NULL
              AND TRIM(c.diagnostic_medical) <> ''
            GROUP BY c.diagnostic_medical
            ORDER BY total DESC
            LIMIT 10
            """, nativeQuery = true)
    List<Object[]> countTopDiagnosticsPeriode(
            @Param("clientId")  Long clientId,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin")   LocalDate dateFin
    );

    @Query(value = """
    SELECT 
        COUNT(DISTINCT p.id_patient) AS total,
        COALESCE(SUM(CASE WHEN lower(p.sexe) = 'masculin' THEN 1 ELSE 0 END), 0) AS masculin,
        COALESCE(SUM(CASE WHEN lower(p.sexe) = 'féminin' THEN 1 ELSE 0 END), 0) AS feminin,
        COALESCE(SUM(CASE WHEN p.assurance = true THEN 1 ELSE 0 END), 0) AS assures,
        COALESCE(SUM(CASE WHEN p.assurance = false OR p.assurance IS NULL THEN 1 ELSE 0 END), 0) AS non_assures
    FROM consultation c
    JOIN patient p ON c.patient_id = p.id_patient
    WHERE c.client_creator_id = :clientId
      AND (:dateDebut  IS NULL OR c.date_consultation >= CAST(:dateDebut  AS date))
      AND (:dateFin    IS NULL OR c.date_consultation <= CAST(:dateFin    AS date))
      AND (:diagnostic     IS NULL OR lower(c.diagnostic_medical::text)      LIKE lower('%' || :diagnostic     || '%'))
      AND (:motif          IS NULL OR lower(c.motif_consultation::text)       LIKE lower('%' || :motif          || '%'))
      AND (:examenClinique IS NULL OR lower(c.resultat_examen_clinique::text) LIKE lower('%' || :examenClinique || '%'))
      AND (:catEvolution   IS NULL OR lower(c.cat_evolution::text)            LIKE lower('%' || :catEvolution   || '%'))
    """,
            nativeQuery = true)
    RapportSummaryProjection getRapportPatientsStats(
            @Param("clientId")       Long clientId,
            @Param("dateDebut")      String dateDebut,
            @Param("dateFin")        String dateFin,
            @Param("diagnostic")     String diagnostic,
            @Param("motif")          String motif,
            @Param("examenClinique") String examenClinique,
            @Param("catEvolution")   String catEvolution
    );

    Integer countAllByPatientIdPatient(Long patientIdPatient);

    // Admin Dash -------------------
    long countByClient_IdClientNot(Long id);

    long countByDateConsultationAndClient_IdClientNot(LocalDate dateConsultation, Long id);

    void deleteConsultationByIdConsultation(Long consultationId);
}
