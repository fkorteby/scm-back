package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.model.Diagnostic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;

@RepositoryRestResource
@PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN')")
@PostFilter("hasPermission(filterObject,'READ')")
public interface DiagnosticRepository extends JpaRepository<Diagnostic, Long> {

    @RestResource(path = "byClient")
    @PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN','REMPLACANT','SECRETAIRE')")
    Page<Diagnostic> findAllByClientCreatorId(@Param("clientId") Long clientCreatorId, Pageable pageable);

    @RestResource(path = "byDiagnostic")
    @PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN','REMPLACANT','SECRETAIRE')")
    Page<Diagnostic> findByClientCreatorIdAndDiagnosticContainingIgnoreCase(@Param("clientId") Long clientCreatorId,
                                                                            String diagnostic, Pageable pageable);

    @RestResource(path = "byAllDiagnostic")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    Page<Diagnostic> findByDiagnosticContainingIgnoreCase(String diagnostic, Pageable pageable);

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN','MEDECIN','REMPLACANT','SECRETAIRE')")
    Optional<Diagnostic> findById(Long aLong);

    @PreAuthorize("hasPermission(#id, 'Diagnostic', 'DELETE')")
    @Override
    void deleteById(Long id);
}
