package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.Projection.PatientsProjection;
import com.simple_cabinet_medical.Backend.model.Client;
import com.simple_cabinet_medical.Backend.model.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@RepositoryRestResource(excerptProjection = PatientsProjection.class)
@PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN','REMPLACANT','SECRETAIRE')")
@PostFilter("hasPermission(filterObject,'CRUD')")
@Transactional
public interface PatientRepository extends JpaRepository<Patient, Long> {

    @RestResource(path = "by-client", rel = "by-client")
    Page<Patient> findByClientCreatorId(@Param("clientId") Long clientId, Pageable pageable);

    @RestResource(path = "byPatient", rel = "byPatient")
    @Query("""
                SELECT p FROM Patient p 
                WHERE p.client.idClient = :clientId 
                  AND (LOWER(p.nom) LIKE LOWER(CONCAT('%', :items, '%')) 
                       OR p.numeroTel LIKE CONCAT(:items, '%'))
            """)
    Page<Patient> searchPatients(@Param("clientId") Long clientId,
                                 @Param("items") String items,
                                 Pageable pageable);


    @RestResource(path = "byPatientAujh", rel = "byPatientAujh")
    @Query("SELECT p FROM Patient p JOIN p.rendezVous r WHERE p.client.idClient = :clientId AND CAST(r.dateRendezVous AS date) = CAST(:date AS date)")
    Page<Patient> findPatientByClientAndRendezVousAujourdhui(
            @Param("clientId") Long clientId,
            @Param("date") Date date,
            Pageable pageable);

    @Query("SELECT p.sexe, COUNT(p.idPatient) " +
            "FROM Patient p " +
            "WHERE p.client.idClient = :idClient " +
            "GROUP BY p.sexe")
    List<Object[]> countPatientsBySexe(@Param("idClient") Long idClient);


    @RestResource(path = "byAllPatient", rel = "byAllPatient")
    @Query("""
                SELECT p FROM Patient p 
                WHERE (LOWER(p.nom) LIKE LOWER(CONCAT('%', :items, '%')) 
                       OR p.numeroTel LIKE CONCAT(:items, '%'))
            """)
    Page<Patient> searchAllPatients( @Param("items") String items, Pageable pageable);

    @RestResource(path = "byAllPatientAujh", rel = "byAllPatientAujh")
    @Query("SELECT p FROM Patient p JOIN p.rendezVous r WHERE CAST(r.dateRendezVous AS date) = CAST(:date AS date)")
    Page<Patient> findPatientByRendezVousAujourdhui(
            @Param("date") Date date,
            Pageable pageable);

    List<Patient> findAllByClientCreatorId(Long id);
}
