package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.Projection.ConsultationsProjection;
import com.simple_cabinet_medical.Backend.model.Client;
import com.simple_cabinet_medical.Backend.model.Consultation;
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

import java.time.LocalDate;
import java.util.List;

@RepositoryRestResource(excerptProjection = ConsultationsProjection.class)
@PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN','REMPLACANT')")
@PostFilter("hasPermission(filterObject,'READ')")
public interface ConsultationRepository extends JpaRepository<Consultation, Long> {

    @PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN','REMPLACANT') and (#consultation.idConsultation == null ? hasPermission(#consultation,'WRITE') : hasPermission(#consultation,'UPDATE'))")
    Consultation save(Consultation consultation);

    @RestResource(path = "by-client", rel = "by-client")
    @PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN','REMPLACANT','SECRETAIRE')")
    Page<Consultation> findByClientCreatorId(@Param("clientId") Long clientId, Pageable pageable);

    @RestResource(path = "by-date", rel = "by-date")
    @PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN','REMPLACANT','SECRETAIRE')")
    List<Consultation> findAllByClientCreatorIdAndDateConsultation(@Param("clientId") Long clientId,
                                                                   @Param("date") LocalDate dateConsultation,
                                                                   Pageable pageable);

    @PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN','REMPLACANT') and hasPermission(#id,'Consultation','DELETE')")
    void deleteById(Long id);

    List<Consultation> findAllByClientCreatorId(Long clientCreatorId);

    List<Consultation> findAllByClientCreatorIdAndDateConsultation(Long clientCreatorId, LocalDate dateConsultation);

    @Query(value = "SELECT EXTRACT(MONTH FROM c.date_consultation) AS mois, COUNT(c.id_consultation) AS total " +
            "FROM consultation c " +
            "WHERE c.client_id = :idClient " +
            "GROUP BY mois " +
            "ORDER BY mois", nativeQuery = true)
    List<Object[]> countConsultationsByMonth(@Param("idClient") Long idClient);


    @RestResource(path = "Allby-date", rel = "Allby-date")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    List<Consultation> findAllByDateConsultation(@Param("date") LocalDate dateConsultation, Pageable pageable);

}
