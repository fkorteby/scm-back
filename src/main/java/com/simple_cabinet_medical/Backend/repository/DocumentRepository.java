package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;


@RepositoryRestResource
@PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN')")
@PostFilter("hasPermission(filterObject,'READ')")
public interface DocumentRepository extends JpaRepository<Document, Long> {

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN','REMPLACANT') and (#document.idDocument == null ? hasPermission(#document,'WRITE') : hasPermission(#document,'UPDATE'))")
    Document save(Document document);

    @PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN','REMPLACANT','SECRETAIRE')")
    Optional<Document> findById(Long id);

    @Override
    @PreAuthorize("hasPermission(#id,'Document','DELETE') and hasAnyAuthority('ADMIN','MEDECIN','REMPLACANT')")
    void deleteById(Long id);
}
