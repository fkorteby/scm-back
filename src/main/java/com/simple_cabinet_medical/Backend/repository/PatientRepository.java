package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.Dto.RapportSummaryProjection;
import com.simple_cabinet_medical.Backend.Projection.PatientsProjection;
import com.simple_cabinet_medical.Backend.model.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@RepositoryRestResource(excerptProjection = PatientsProjection.class)
@PostFilter("hasPermission(filterObject,'READ')")
@Transactional
public interface PatientRepository extends JpaRepository<Patient, Long> {

    @PreAuthorize("((#patient.idPatient == null or #patient.idPatient == 0) ? hasPermission(#patient,'WRITE') : hasPermission(#patient,'UPDATE'))")
    Patient save(Patient patient);

    @RestResource(path = "by-client", rel = "by-client")
    Page<Patient> findByClientCreatorIdOrderByNomAscPrenomAsc(@Param("clientId") Long clientId, Pageable pageable);


    @RestResource(path = "byPatient", rel = "byPatient")
    @Query("""
                SELECT p FROM Patient p
                WHERE p.clientCreatorId = :clientId
                  AND (
                       LOWER(p.nom) LIKE LOWER(CONCAT('%', :items, '%'))
                    OR LOWER(p.prenom) LIKE LOWER(CONCAT('%', :items, '%'))
                    OR p.numeroTel LIKE CONCAT(:items, '%')
                    OR LOWER(p.cin) LIKE LOWER(CONCAT('%', :items, '%'))
                    OR LOWER(p.numeroSecuriteSociale) LIKE LOWER(CONCAT('%', :items, '%'))
                    OR LOWER(p.passport) LIKE LOWER(CONCAT('%', :items, '%'))
                    OR LOWER(p.acteNaissance) LIKE LOWER(CONCAT('%', :items, '%'))
                  )
                order by p.nom,p.prenom ASC
            """)
    Page<Patient> searchPatients(@Param("clientId") Long clientId,
                                 @Param("items") String items,
                                 Pageable pageable);

    @RestResource(path = "byPatientAujh", rel = "byPatientAujh")
    @Query(""" 
                SELECT p FROM Patient p JOIN p.rendezVous r 
                WHERE p.clientCreatorId = :clientId AND CAST(r.dateRendezVous AS date) = CAST(:date AS date)
                order by p.nom,p.prenom ASC""")
    Page<Patient> findPatientByClientAndRendezVousAujourdhui(
            @Param("clientId") Long clientId,
            @Param("date") @DateTimeFormat(pattern = "MM/dd/yyyy") Date date,
            Pageable pageable);

    @RestResource(path = "byConsultationAujh", rel = "byConsultationAujh")
    @Query(""" 
            SELECT p FROM Patient p JOIN p.consultations c 
            WHERE p.clientCreatorId = :clientId AND c.dateConsultation = :date 
            ORDER BY p.nom, p.prenom ASC""")
    Page<Patient> findPatientByClientAndConsultationAujourdhui(
            @Param("clientId") Long clientId,
            @Param("date") @DateTimeFormat(pattern = "MM/dd/yyyy") LocalDate date,
            Pageable pageable);

    @Query("SELECT p.sexe, COUNT(p.idPatient) " +
            "FROM Patient p " +
            "WHERE p.clientCreatorId = :idClient " +
            "GROUP BY p.sexe")
    List<Object[]> countPatientsBySexe(@Param("idClient") Long idClient);

    @RestResource(path = "byAllPatient", rel = "byAllPatient")
    @Query("""
                SELECT p FROM Patient p
                WHERE 
                      LOWER(p.nom) LIKE LOWER(CONCAT('%', :items, '%'))
                   OR LOWER(p.prenom) LIKE LOWER(CONCAT('%', :items, '%'))
                   OR p.numeroTel LIKE CONCAT(:items, '%')
                   OR LOWER(p.cin) LIKE LOWER(CONCAT('%', :items, '%'))
                   OR LOWER(p.numeroSecuriteSociale) LIKE LOWER(CONCAT('%', :items, '%'))
                   OR LOWER(p.passport) LIKE LOWER(CONCAT('%', :items, '%'))
                   OR LOWER(p.acteNaissance) LIKE LOWER(CONCAT('%', :items, '%'))
                order by p.nom,p.prenom ASC
            """)
    Page<Patient> searchAllPatients(@Param("items") String items, Pageable pageable);

    @RestResource(path = "byAllPatientAujh", rel = "byAllPatientAujh")
    @Query(""" 
            SELECT p FROM Patient p JOIN p.consultations c 
            WHERE c.dateConsultation = :date 
            ORDER BY p.nom, p.prenom ASC               
            """)
    Page<Patient> findPatientByRendezVousAujourdhui(
            @Param("date") @DateTimeFormat(pattern = "MM/dd/yyyy") LocalDate date,
            Pageable pageable);

    List<Patient> findAllByClientCreatorIdOrderByNomAscPrenomAsc(Long id);

    @PreAuthorize("hasPermission(#id, 'Patient', 'DELETE')")
    @Override
    void deleteById(Long id);

    @Query("""
            SELECT COUNT(p.idPatient) 
            FROM Patient p 
            WHERE p.clientCreatorId = :clientCreatorId
            """)
    Long nombrePatientByClient(Long clientCreatorId);

    ///  //////////// RAPPORTS /////////////////
    @Query(value = """
    SELECT p.*, sub.derniere_consultation
    FROM patient p
    INNER JOIN (
        SELECT patient_id, MAX(date_consultation) as derniere_consultation
        FROM consultation
        GROUP BY patient_id
    ) sub ON p.id_patient = sub.patient_id
    WHERE p.client_creator_id = :clientId
      AND (:dateDebut IS NULL OR sub.derniere_consultation >= CAST(:dateDebut AS date))
      AND (:dateFin IS NULL OR sub.derniere_consultation <= CAST(:dateFin AS date))
      AND (:sexe IS NULL OR p.sexe = :sexe)
      AND (:situation IS NULL OR p.situation = :situation)
      AND (:assurance IS NULL OR p.assurance = CAST(:assurance AS boolean))
      AND (:ageMin IS NULL OR (EXTRACT(YEAR FROM CURRENT_DATE) - EXTRACT(YEAR FROM p.date_naissance)) >= :ageMin)
      AND (:ageMax IS NULL OR (EXTRACT(YEAR FROM CURRENT_DATE) - EXTRACT(YEAR FROM p.date_naissance)) <= :ageMax)
    ORDER BY p.nom, p.prenom ASC
    """,
            countQuery = """
    SELECT COUNT(p.id_patient)
    FROM patient p
    INNER JOIN (
        SELECT patient_id, MAX(date_consultation) as derniere_consultation
        FROM consultation
        GROUP BY patient_id
    ) sub ON p.id_patient = sub.patient_id
    WHERE p.client_creator_id = :clientId
      AND (:dateDebut IS NULL OR sub.derniere_consultation >= CAST(:dateDebut AS date))
      AND (:dateFin IS NULL OR sub.derniere_consultation <= CAST(:dateFin AS date))
      AND (:sexe IS NULL OR p.sexe = :sexe)
      AND (:situation IS NULL OR p.situation = :situation)
      AND (:assurance IS NULL OR p.assurance = CAST(:assurance AS boolean))
      AND (:ageMin IS NULL OR (EXTRACT(YEAR FROM CURRENT_DATE) - EXTRACT(YEAR FROM p.date_naissance)) >= :ageMin)
      AND (:ageMax IS NULL OR (EXTRACT(YEAR FROM CURRENT_DATE) - EXTRACT(YEAR FROM p.date_naissance)) <= :ageMax)
    """,
            nativeQuery = true)
    Page<Patient> findRapportByClient(
            @Param("clientId") Long clientId,
            @Param("dateDebut") String dateDebut,
            @Param("dateFin") String dateFin,
            @Param("sexe") String sexe,
            @Param("situation") String situation,
            @Param("assurance") String assurance,
            @Param("ageMin") Integer ageMin,
            @Param("ageMax") Integer ageMax,
            Pageable pageable);

    @Query(value = """
    SELECT p.*, sub.derniere_consultation
    FROM patient p
    INNER JOIN (
        SELECT patient_id, MAX(date_consultation) as derniere_consultation
        FROM consultation
        GROUP BY patient_id
    ) sub ON p.id_patient = sub.patient_id
    WHERE (:dateDebut IS NULL OR sub.derniere_consultation >= CAST(:dateDebut AS date))
      AND (:dateFin IS NULL OR sub.derniere_consultation <= CAST(:dateFin AS date))
      AND (:sexe IS NULL OR p.sexe = :sexe)
      AND (:situation IS NULL OR p.situation = :situation)
      AND (:assurance IS NULL OR p.assurance = CAST(:assurance AS boolean))
      AND (:ageMin IS NULL OR (EXTRACT(YEAR FROM CURRENT_DATE) - EXTRACT(YEAR FROM p.date_naissance)) >= :ageMin)
      AND (:ageMax IS NULL OR (EXTRACT(YEAR FROM CURRENT_DATE) - EXTRACT(YEAR FROM p.date_naissance)) <= :ageMax)
    ORDER BY p.nom, p.prenom ASC
    """,
            countQuery = """
    SELECT COUNT(p.id_patient)
    FROM patient p
    INNER JOIN (
        SELECT patient_id, MAX(date_consultation) as derniere_consultation
        FROM consultation
        GROUP BY patient_id
    ) sub ON p.id_patient = sub.patient_id
    WHERE (:dateDebut IS NULL OR sub.derniere_consultation >= CAST(:dateDebut AS date))
      AND (:dateFin IS NULL OR sub.derniere_consultation <= CAST(:dateFin AS date))
      AND (:sexe IS NULL OR p.sexe = :sexe)
      AND (:situation IS NULL OR p.situation = :situation)
      AND (:assurance IS NULL OR p.assurance = CAST(:assurance AS boolean))
      AND (:ageMin IS NULL OR (EXTRACT(YEAR FROM CURRENT_DATE) - EXTRACT(YEAR FROM p.date_naissance)) >= :ageMin)
      AND (:ageMax IS NULL OR (EXTRACT(YEAR FROM CURRENT_DATE) - EXTRACT(YEAR FROM p.date_naissance)) <= :ageMax)
    """,
            nativeQuery = true)
    Page<Patient> findRapportAll(
            @Param("dateDebut") String dateDebut,
            @Param("dateFin") String dateFin,
            @Param("sexe") String sexe,
            @Param("situation") String situation,
            @Param("assurance") String assurance,
            @Param("ageMin") Integer ageMin,
            @Param("ageMax") Integer ageMax,
            Pageable pageable);
    /**
     * Comptage par sexe pour le récapitulatif (filtré client).
     */
    @Query(value = """
            SELECT p.sexe, COUNT(p.id_patient)
            FROM patient p
            WHERE p.client_creator_id= :clientId
              AND (:dateDebut IS NULL OR DATE(p.date_creation) >= CAST(:dateDebut AS date))
              AND (:dateFin   IS NULL OR DATE(p.date_creation) <= CAST(:dateFin   AS date))
              AND (:sexe      IS NULL OR p.sexe      = :sexe)
              AND (:situation IS NULL OR p.situation = :situation)
              AND (:assurance IS NULL OR p.assurance = CAST(:assurance AS boolean))
              AND (:ageMin    IS NULL OR
                   (EXTRACT(YEAR FROM CURRENT_DATE) - EXTRACT(YEAR FROM p.date_naissance)) >= CAST(:ageMin AS integer))
              AND (:ageMax    IS NULL OR
                   (EXTRACT(YEAR FROM CURRENT_DATE) - EXTRACT(YEAR FROM p.date_naissance)) <= CAST(:ageMax AS integer))
            GROUP BY p.sexe
            """, nativeQuery = true)
    List<Object[]> countRapportBySexe(
            @Param("clientId") Long clientId,
            @Param("dateDebut") String dateDebut,
            @Param("dateFin") String dateFin,
            @Param("sexe") String sexe,
            @Param("situation") String situation,
            @Param("assurance") String assurance,
            @Param("ageMin") Integer ageMin,
            @Param("ageMax") Integer ageMax
    );

    /**
     * Comptage par assurance pour le récapitulatif (filtré client).
     */
    @Query(value = """
            SELECT p.assurance, COUNT(p.id_patient)
            FROM patient p
            WHERE p.clientCreatorId = :clientId
              AND (:dateDebut IS NULL OR DATE(p.date_creation) >= CAST(:dateDebut AS date))
              AND (:dateFin   IS NULL OR DATE(p.date_creation) <= CAST(:dateFin   AS date))
              AND (:sexe      IS NULL OR p.sexe      = :sexe)
              AND (:situation IS NULL OR p.situation = :situation)
              AND (:assurance IS NULL OR p.assurance = CAST(:assurance AS boolean))
              AND (:ageMin    IS NULL OR
                   (EXTRACT(YEAR FROM CURRENT_DATE) - EXTRACT(YEAR FROM p.date_naissance)) >= CAST(:ageMin AS integer))
              AND (:ageMax    IS NULL OR
                   (EXTRACT(YEAR FROM CURRENT_DATE) - EXTRACT(YEAR FROM p.date_naissance)) <= CAST(:ageMax AS integer))
            GROUP BY p.assurance
            """, nativeQuery = true)
    List<Object[]> countRapportByAssurance(
            @Param("clientId") Long clientId,
            @Param("dateDebut") String dateDebut,
            @Param("dateFin") String dateFin,
            @Param("sexe") String sexe,
            @Param("situation") String situation,
            @Param("assurance") String assurance,
            @Param("ageMin") Integer ageMin,
            @Param("ageMax") Integer ageMax
    );

    @Query(value = """
            SELECT
                p.sexe                  AS sexe,
                COUNT(c.id_consultation) AS total
            FROM consultation c
            INNER JOIN patient p ON c.patient_id = p.id_patient
            WHERE c.client_creator_id = :clientId
              AND c.date_consultation >= :dateDebut
              AND c.date_consultation <= :dateFin
            GROUP BY p.sexe
            """, nativeQuery = true)
    List<Object[]> countConsultationsBySexePeriodeRaw(
            @Param("clientId") Long clientId,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin
    );

    @RestResource(exported = false)
    @Query(value = """
                SELECT 
                    COUNT(DISTINCT p.id_patient),
                    COUNT(DISTINCT CASE WHEN LOWER(p.sexe) = 'masculin' THEN p.id_patient END),
                    COUNT(DISTINCT CASE WHEN LOWER(p.sexe) = 'féminin' OR LOWER(p.sexe) = 'feminin' THEN p.id_patient END),
                    COUNT(DISTINCT CASE WHEN p.assurance = true  THEN p.id_patient END),
                    COUNT(DISTINCT CASE WHEN p.assurance = false OR p.assurance IS NULL THEN p.id_patient END)
                FROM patient p
                WHERE p.client_creator_id = :clientId
                  AND (:dateDebut IS NULL OR DATE(p.date_creation) >= CAST(:dateDebut AS date))
                  AND (:dateFin   IS NULL OR DATE(p.date_creation) <= CAST(:dateFin AS date))
                  AND (:sexe        IS NULL OR p.sexe = :sexe)
                  AND (:situation   IS NULL OR p.situation = :situation)
                  AND (:assurance   IS NULL OR p.assurance = CAST(:assurance AS boolean))
                  AND (:ageMin IS NULL OR (EXTRACT(YEAR FROM CURRENT_DATE) - EXTRACT(YEAR FROM p.date_naissance)) >= :ageMin)
                  AND (:ageMax IS NULL OR (EXTRACT(YEAR FROM CURRENT_DATE) - EXTRACT(YEAR FROM p.date_naissance)) <= :ageMax)
            """, nativeQuery = true)
    List<Object[]> getSummaryByClient(
            @Param("clientId") Long clientId,
            @Param("dateDebut") String dateDebut,
            @Param("dateFin") String dateFin,
            @Param("sexe") String sexe,
            @Param("situation") String situation,
            @Param("assurance") String assurance,
            @Param("ageMin") Integer ageMin,
            @Param("ageMax") Integer ageMax);


    @Query(value = """
                SELECT p.* FROM patient p
                INNER JOIN consultation c ON c.patient_id = p.id_patient
                WHERE p.client_creator_id = :clientId
                  AND (:dateDebut IS NULL OR c.date_consultation >= CAST(:dateDebut AS date))
                  AND (:dateFin IS NULL OR c.date_consultation <= CAST(:dateFin AS date))
                  AND (CAST(:sexe AS text) IS NULL OR p.sexe = CAST(:sexe AS text))
                  AND (CAST(:situation AS text) IS NULL OR p.situation = CAST(:situation AS text))
                  AND (CAST(:assurance AS text) IS NULL OR p.assurance = CAST(:assurance AS boolean))
                  AND (CAST(:ageMin AS integer) IS NULL OR (EXTRACT(YEAR FROM CURRENT_DATE) - EXTRACT(YEAR FROM p.date_naissance)) >= CAST(:ageMin AS integer))
                  AND (CAST(:ageMax AS integer) IS NULL OR (EXTRACT(YEAR FROM CURRENT_DATE) - EXTRACT(YEAR FROM p.date_naissance)) <= CAST(:ageMax AS integer))
                GROUP BY p.id_patient
                order by p.nom,p.prenom ASC
            """,
            countQuery = """
                        SELECT COUNT(DISTINCT p.id_patient) 
                        FROM patient p
                        INNER JOIN consultation c ON c.patient_id = p.id_patient
                        WHERE p.client_creator_id = :clientId
                          AND (:dateDebut IS NULL OR c.date_consultation >= CAST(:dateDebut AS date))
                          AND (:dateFin IS NULL OR c.date_consultation <= CAST(:dateFin AS date))
                          AND (CAST(:sexe AS text) IS NULL OR p.sexe = CAST(:sexe AS text))
                          AND (CAST(:situation AS text) IS NULL OR p.situation = CAST(:situation AS text))
                          AND (CAST(:assurance AS text) IS NULL OR p.assurance = CAST(:assurance AS boolean))
                          AND (CAST(:ageMin AS integer) IS NULL OR (EXTRACT(YEAR FROM CURRENT_DATE) - EXTRACT(YEAR FROM p.date_naissance)) >= CAST(:ageMin AS integer))
                          AND (CAST(:ageMax AS integer) IS NULL OR (EXTRACT(YEAR FROM CURRENT_DATE) - EXTRACT(YEAR FROM p.date_naissance)) <= CAST(:ageMax AS integer))
                    """, nativeQuery = true)
    List<Patient> findRapportForExportByClient(
            @Param("clientId") Long clientId,
            @Param("dateDebut") String dateDebut,
            @Param("dateFin") String dateFin,
            @Param("sexe") String sexe,
            @Param("situation") String situation,
            @Param("assurance") String assurance,
            @Param("ageMin") Integer ageMin,
            @Param("ageMax") Integer ageMax);

    @Query(value = """
                SELECT p.* FROM patient p
                INNER JOIN consultation c ON c.patient_id = p.id_patient
                WHERE (:dateDebut IS NULL OR c.date_consultation >= CAST(:dateDebut AS date))
                  AND (:dateFin IS NULL OR c.date_consultation <= CAST(:dateFin AS date))
                  AND (CAST(:sexe AS text) IS NULL OR p.sexe = CAST(:sexe AS text))
                  AND (CAST(:situation AS text) IS NULL OR p.situation = CAST(:situation AS text))
                  AND (CAST(:assurance AS text) IS NULL OR p.assurance = CAST(:assurance AS boolean))
                  AND (CAST(:ageMin AS integer) IS NULL OR (EXTRACT(YEAR FROM CURRENT_DATE) - EXTRACT(YEAR FROM p.date_naissance)) >= CAST(:ageMin AS integer))
                  AND (CAST(:ageMax AS integer) IS NULL OR (EXTRACT(YEAR FROM CURRENT_DATE) - EXTRACT(YEAR FROM p.date_naissance)) <= CAST(:ageMax AS integer))
                GROUP BY p.id_patient
                order by p.nom,p.prenom ASC
            """,
            countQuery = """
                        SELECT COUNT(DISTINCT p.id_patient) 
                        FROM patient p
                        INNER JOIN consultation c ON c.patient_id = p.id_patient
                        WHERE (:dateDebut IS NULL OR c.date_consultation >= CAST(:dateDebut AS date))
                          AND (:dateFin IS NULL OR c.date_consultation <= CAST(:dateFin AS date))
                          AND (CAST(:sexe AS text) IS NULL OR p.sexe = CAST(:sexe AS text))
                          AND (CAST(:situation AS text) IS NULL OR p.situation = CAST(:situation AS text))
                          AND (CAST(:assurance AS text) IS NULL OR p.assurance = CAST(:assurance AS boolean))
                          AND (CAST(:ageMin AS integer) IS NULL OR (EXTRACT(YEAR FROM CURRENT_DATE) - EXTRACT(YEAR FROM p.date_naissance)) >= CAST(:ageMin AS integer))
                          AND (CAST(:ageMax AS integer) IS NULL OR (EXTRACT(YEAR FROM CURRENT_DATE) - EXTRACT(YEAR FROM p.date_naissance)) <= CAST(:ageMax AS integer))
                    """, nativeQuery = true)
    List<Patient> findRapportAllForExport(
            @Param("dateDebut") String dateDebut,
            @Param("dateFin") String dateFin,
            @Param("sexe") String sexe,
            @Param("situation") String situation,
            @Param("assurance") String assurance,
            @Param("ageMin") Integer ageMin,
            @Param("ageMax") Integer ageMax);

    @Query(value = """
        SELECT 
            COUNT(p.id_patient) as total,
            SUM(CASE WHEN p.sexe = 'Masculin' THEN 1 ELSE 0 END) as masculin,
            SUM(CASE WHEN p.sexe = 'Féminin' THEN 1 ELSE 0 END) as feminin,
            SUM(CASE WHEN p.assurance = true THEN 1 ELSE 0 END) as assures,
            SUM(CASE WHEN p.assurance = false THEN 1 ELSE 0 END) as nonAssures
        FROM (
            SELECT DISTINCT p.id_patient, p.sexe, p.assurance
            FROM patient p
            INNER JOIN consultation c ON p.id_patient = c.patient_id
            WHERE (:clientId IS NULL OR p.client_creator_id= :clientId)
              AND (:dateDebut IS NULL OR c.date_consultation >= CAST(:dateDebut AS date))
              AND (:dateFin IS NULL OR c.date_consultation <= CAST(:dateFin AS date))
              AND (:sexe IS NULL OR p.sexe = :sexe)
              AND (:situation IS NULL OR p.situation = :situation)
              AND (:assurance IS NULL OR p.assurance = CAST(:assurance AS boolean))
              AND (:ageMin IS NULL OR (EXTRACT(YEAR FROM CURRENT_DATE) - EXTRACT(YEAR FROM p.date_naissance)) >= :ageMin)
              AND (:ageMax IS NULL OR (EXTRACT(YEAR FROM CURRENT_DATE) - EXTRACT(YEAR FROM p.date_naissance)) <= :ageMax)
        ) as p
        """, nativeQuery = true)
    RapportSummaryProjection getStatsByClient(Long clientId, String dateDebut, String dateFin, String sexe,
                                              String situation, String assurance, Integer ageMin, Integer ageMax);

    @Query(value = """
    SELECT 
        COUNT(DISTINCT p.id_patient) as total,
        COUNT(DISTINCT CASE WHEN p.sexe = 'Masculin' THEN p.id_patient END) as masculin,
        COUNT(DISTINCT CASE WHEN p.sexe = 'Féminin' THEN p.id_patient END) as feminin,
        COUNT(DISTINCT CASE WHEN p.assurance = true THEN p.id_patient END) as assures,
        COUNT(DISTINCT CASE WHEN p.assurance = false THEN p.id_patient END) as nonAssures
    FROM patient p
    INNER JOIN consultation c ON p.id_patient = c.patient_id
    WHERE (:dateDebut IS NULL OR c.date_consultation >= CAST(:dateDebut AS date))
      AND (:dateFin IS NULL OR c.date_consultation <= CAST(:dateFin AS date))
      AND (:sexe IS NULL OR p.sexe = :sexe)
      AND (:situation IS NULL OR p.situation = :situation)
      AND (:assurance IS NULL OR p.assurance = CAST(:assurance AS boolean))
      AND (:ageMin IS NULL OR (EXTRACT(YEAR FROM CURRENT_DATE) - EXTRACT(YEAR FROM p.date_naissance)) >= :ageMin)
      AND (:ageMax IS NULL OR (EXTRACT(YEAR FROM CURRENT_DATE) - EXTRACT(YEAR FROM p.date_naissance)) <= :ageMax)
    """, nativeQuery = true)
    List<Object[]> getStats(String dateDebut, String dateFin, String sexe,
                            String situation, String assurance, Integer ageMin, Integer ageMax);


    // Admin Dash

    long countByClient_IdClientNot(Long id);

    long countByDateCreationAfterAndClient_IdClientNot(Date startDate, Long id);
}