package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.Projection.RendezVousProjection;
import com.simple_cabinet_medical.Backend.model.RendezVous;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.LocalDate;
import java.util.List;

@RepositoryRestResource(excerptProjection = RendezVousProjection.class)
@PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN','REMPLACANT','SECRETAIRE')")
@PostFilter("hasPermission(filterObject,'CRUD')")
public interface RendezVousRepository extends JpaRepository<RendezVous, Long> {

    @RestResource(path = "by-client", rel = "by-client")
    Page<RendezVous> findRendezVousByClientCreatorId(@Param("clientId") Long idClient, Pageable pageable);

    @RestResource(path = "by-date", rel = "by-date")
    List<RendezVous> findRendezVousByClientCreatorIdAndDateRendezVous(@Param("clientId") Long client, @Param("dateRendezVous") LocalDate dateRendezVous);

    @RestResource(path = "Allby-date", rel = "Allby-date")
    List<RendezVous> findRendezVousByDateRendezVous(@Param("dateRendezVous") LocalDate dateRendezVous);


    List<RendezVous> findAllByClientCreatorIdAndDateRendezVous(Long clientCreatorId, LocalDate dateRendezVous);

}
