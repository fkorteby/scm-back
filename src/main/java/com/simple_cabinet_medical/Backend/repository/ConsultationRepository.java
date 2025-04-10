package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.model.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RepositoryRestResource
@PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN','REMPLACANT')")
@PostFilter("hasPermission(filterObject,'READ')")
public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN','REMPLACANT') and (#consultation.idConsultation == null ? hasPermission(#consultation,'WRITE') : hasPermission(#consultation,'UPDATE'))")
    Consultation save(Consultation consultation);

    @PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN','REMPLACANT','SECRETAIRE')")
    List<Consultation> findAll();

    @Override
    @PreAuthorize("hasPermission(#id,'Consultation','DELETE') and hasAnyAuthority('ADMIN','MEDECIN','REMPLACANT')")
    void deleteById(Long id);
}
