package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.model.Motif;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Collection;
import java.util.Optional;

@RepositoryRestResource
@PostFilter("hasPermission(filterObject,'READ')")
public interface MotifRepository extends JpaRepository<Motif, Long> {

    @PreAuthorize("((#motif.idMotif == null or #motif.idMotif == 0) ? hasPermission(#motif,'WRITE') : hasPermission(#motif,'UPDATE'))")
    Motif save(Motif motif);

    @RestResource(path = "byClient")
    Page<Motif> findAllByClientCreatorIdIn(@Param("clientId") Collection<Long> clientCreatorIds, Pageable pageable);

    @RestResource(path = "byMotif")
    Page<Motif> findByClientCreatorIdInAndMotifContainingIgnoreCase(@Param("clientId") Collection<Long> clientCreatorIds, String motif, Pageable pageable);

    @RestResource(path = "byAllMotif")
    Page<Motif> findByMotifContainingIgnoreCase(String motif, Pageable pageable);

    @Override
    Optional<Motif> findById(Long aLong);

    @PreAuthorize("hasPermission(#id, 'Motif', 'DELETE')")
    @Override
    void deleteById(Long id);
}
