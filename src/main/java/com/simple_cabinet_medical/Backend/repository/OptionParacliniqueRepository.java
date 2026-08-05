package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.model.OptionParaclinique;
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
public interface OptionParacliniqueRepository extends JpaRepository<OptionParaclinique, Long> {

    //@PreAuthorize("((optionParaclinique.idOptionParaclinique == null or #optionParaclinique.idOptionParaclinique == 0 ) ? hasPermission(#optionParaclinique,'WRITE') : hasPermission(#optionParaclinique,'UPDATE'))")
    OptionParaclinique save(OptionParaclinique optionParaclinique);

    @RestResource(path = "byClient")
    Page<OptionParaclinique> findAllByClientCreatorIdIn(@Param("clientId") Collection<Long> clientCreatorIds, Pageable pageable);

    @RestResource(path = "byOption")
    Page<OptionParaclinique> findByClientCreatorIdInAndOptionContainingIgnoreCase(
            @Param("clientId") Collection<Long> clientCreatorIds, String option, Pageable pageable);

    @RestResource(path = "allByOption")
    Page<OptionParaclinique> findAllByOptionContainingIgnoreCase(String option, Pageable pageable);

    @Override
    Optional<OptionParaclinique> findById(Long aLong);

    @PreAuthorize("hasPermission(#id, 'OptionParaclinique', 'DELETE')")
    @Override
    void deleteById(Long id);
}
