package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.model.Conduite;
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
public interface ConduiteRepository extends JpaRepository<Conduite, Long> {

    @PreAuthorize("((#conduite.idConduite == null or #conduite.idConduite == 0)? hasPermission(#conduite,'WRITE') : hasPermission(#conduite,'UPDATE'))")
    Conduite save(Conduite conduite);

    @RestResource(path = "byClient")
    Page<Conduite> findAllByClientCreatorIdIn(@Param("clientId") Collection<Long> clientCreatorIds, Pageable pageable);

    @RestResource(path = "byConduite")
    Page<Conduite> findByClientCreatorIdInAndConduiteContainingIgnoreCase(@Param("clientId")
                                                                          Collection<Long> clientCreatorIds,
                                                                          String conduite, Pageable pageable);

    @RestResource(path = "byAllConduite")
    Page<Conduite> findByConduiteContainingIgnoreCase(String conduite, Pageable pageable);

    @Override
    Optional<Conduite> findById(Long aLong);

    @PreAuthorize("hasPermission(#id, 'Conduite', 'DELETE')")
    @Override
    void deleteById(Long id);
}
