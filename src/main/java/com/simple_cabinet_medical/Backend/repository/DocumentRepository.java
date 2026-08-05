package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.Projection.DocumentProjection;
import com.simple_cabinet_medical.Backend.model.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;


@RepositoryRestResource(excerptProjection = DocumentProjection.class)
@PostFilter("hasPermission(filterObject,'READ')")
public interface DocumentRepository extends JpaRepository<Document, Long> {

    @PreAuthorize("((#document.idDocument == null or #document.idDocument == 0) ? hasPermission(#document,'WRITE') : hasPermission(#document,'UPDATE'))")
    Document save(Document document);

    @RestResource(path = "by-client", rel = "by-client")
    Page<Document> findByClientCreatorId(@Param("clientId") Long clientId, Pageable pageable);

    @RestResource(path = "by-nom", rel = "by-nom")
    Page<Document> findByNomDocumentIsContainingIgnoreCase(@Param("nom") String nomDocument, Pageable pageable);

    @RestResource(path = "by-clientAndNom", rel = "by-clientAndNom")
    Page<Document> findByClientCreatorIdAndNomDocumentIsContainingIgnoreCase(@Param("clientId") Long clientId,@Param("nom") String nomDocument, Pageable pageable);

    @PreAuthorize("hasPermission(#id,'Document','DELETE')")
    void deleteById(Long id);


    @RestResource(path = "by-clientAndPatient", rel = "by-clientAndPatient")
    @Query("""
    SELECT d
    FROM Document d
    WHERE d.patient.clientCreatorId = :clientId
      AND (
            LOWER(d.patient.nom) LIKE LOWER(CONCAT('%', :patient, '%'))
         OR LOWER(d.patient.prenom) LIKE LOWER(CONCAT('%', :patient, '%'))
      )
""")
    Page<Document> findByClientAndPatient(
            @Param("clientId") Long clientId,
            @Param("patient") String patient,
            Pageable pageable);


    @RestResource(path = "by-patient", rel = "by-patient")
    @Query("""
    SELECT d
    FROM Document d
    WHERE LOWER(d.patient.nom) LIKE LOWER(CONCAT('%', :patient, '%'))
       OR LOWER(d.patient.prenom) LIKE LOWER(CONCAT('%', :patient, '%'))
""")
    Page<Document> findByPatient(
            @Param("patient") String patient,
            Pageable pageable);


    Integer countAllByPatientIdPatient(Long patientIdPatient);
}
