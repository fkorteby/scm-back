package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.Projection.RendezVousProjection;
import com.simple_cabinet_medical.Backend.model.RendezVous;
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

@RepositoryRestResource(excerptProjection = RendezVousProjection.class)
@PostFilter("hasPermission(filterObject,'READ')")
public interface RendezVousRepository extends JpaRepository<RendezVous, Long> {

    @PreAuthorize("((#rendezVous.idRendezVous == null or #rendezVous.idRendezVous ==0)? hasPermission(#rendezVous,'WRITE') : hasPermission(#rendezVous,'UPDATE'))")
    RendezVous save(RendezVous rendezVous);

    @RestResource(path = "by-client", rel = "by-client")
    Page<RendezVous> findRendezVousByClientCreatorId(@Param("clientId") Long idClient, Pageable pageable);

    @RestResource(path = "by-date", rel = "by-date")
    List<RendezVous> findRendezVousByClientCreatorIdAndDateRendezVous(
            @Param("clientId") Long client,
            @Param("dateRendezVous") LocalDate dateRendezVous
    );

    /**
     * Nouvelle méthode pour récupérer les rendez-vous par plage de dates
     * Utilisée pour le filtrage par mois/semaine/jour dans l'agenda
     */
    @RestResource(path = "by-date-range", rel = "by-date-range")
    @Query("""
            SELECT r FROM RendezVous r 
            WHERE r.clientCreatorId = :clientId 
            AND r.dateRendezVous BETWEEN :startDate AND :endDate
            ORDER BY r.dateRendezVous ASC, r.heureRendezVous ASC
            """)
    List<RendezVous> findRendezVousByClientCreatorIdAndDateRendezVousBetween(
            @Param("clientId") Long clientId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * Version paginée de la recherche par plage de dates
     */
    @RestResource(path = "by-date-range-pageable", rel = "by-date-range-pageable")
    @Query("""
            SELECT r FROM RendezVous r 
            WHERE r.clientCreatorId = :clientId 
            AND r.dateRendezVous BETWEEN :startDate AND :endDate
            ORDER BY r.dateRendezVous ASC, r.heureRendezVous ASC
            """)
    Page<RendezVous> findRendezVousByClientCreatorIdAndDateRendezVousBetweenPageable(
            @Param("clientId") Long clientId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    @RestResource(path = "Allby-date", rel = "Allby-date")
    List<RendezVous> findRendezVousByDateRendezVous(@Param("dateRendezVous") LocalDate dateRendezVous);

    /**
     * Recherche tous les rendez-vous (sans filtre client) par plage de dates
     * Pour les utilisateurs admin
     */
    @RestResource(path = "all-by-date-range", rel = "all-by-date-range")
    @Query("""
            SELECT r FROM RendezVous r 
            WHERE r.dateRendezVous BETWEEN :startDate AND :endDate
            ORDER BY r.dateRendezVous ASC, r.heureRendezVous ASC
            """)
    List<RendezVous> findAllByDateRendezVousBetween(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    List<RendezVous> findAllByClientCreatorIdAndDateRendezVous(Long clientCreatorId, LocalDate dateRendezVous);

    @PreAuthorize("hasPermission(#id, 'RendezVous', 'DELETE')")
    @Override
    void deleteById(Long id);

    @Query("""
            SELECT count(r.idRendezVous) 
            FROM RendezVous r
            WHERE r.clientCreatorId = :idClient
            AND r.dateRendezVous = :date
            """)
    Optional<Long> countRendezVousOfTodayByClientCreatorId(Long idClient, LocalDate date);

    /**
     * Compter les rendez-vous dans une plage de dates
     */
    @Query("""
            SELECT count(r.idRendezVous) 
            FROM RendezVous r
            WHERE r.clientCreatorId = :idClient
            AND r.dateRendezVous BETWEEN :startDate AND :endDate
            """)
    Optional<Long> countRendezVousByClientCreatorIdAndDateRange(
            @Param("idClient") Long idClient,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * Récupérer les rendez-vous d'un patient spécifique dans une plage de dates
     */
    @Query("""
            SELECT r FROM RendezVous r 
            WHERE r.clientCreatorId = :clientId 
            AND r.patient.idPatient = :patientId
            AND r.dateRendezVous BETWEEN :startDate AND :endDate
            ORDER BY r.dateRendezVous ASC, r.heureRendezVous ASC
            """)
    List<RendezVous> findRendezVousByClientAndPatientAndDateRange(
            @Param("clientId") Long clientId,
            @Param("patientId") Long patientId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * Récupérer les rendez-vous par statut dans une plage de dates
     */
    @Query("""
            SELECT r FROM RendezVous r 
            WHERE r.clientCreatorId = :clientId 
            AND r.statusRendezVous = :status
            AND r.dateRendezVous BETWEEN :startDate AND :endDate
            ORDER BY r.dateRendezVous ASC, r.heureRendezVous ASC
            """)
    List<RendezVous> findRendezVousByClientAndStatusAndDateRange(
            @Param("clientId") Long clientId,
            @Param("status") String status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
    @Query(value = """
            SELECT
                DATE(r.date_rendez_vous)  AS jour,
                COUNT(r.id_rendez_vous)   AS total
            FROM rendez_vous r
            INNER JOIN patient p ON r.patient_id = p.id_patient
            WHERE p.client_id = :clientId
              AND r.date_rendez_vous >= :dateDebut
              AND r.date_rendez_vous <= :dateFin
            GROUP BY DATE(r.date_rendez_vous)
            ORDER BY jour
            """, nativeQuery = true)
    List<Object[]> countRendezVousParJourPeriode(
            @Param("clientId")  Long clientId,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin")   LocalDate dateFin
    );

    Integer countAllByPatientIdPatient(Long patientIdPatient);
}