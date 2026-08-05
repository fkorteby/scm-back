package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.model.ExamenParacliniqueType;
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
@PostFilter("hasPermission(filterObject,'READ')")
public interface ExamenParacliniqueTypeRepository extends JpaRepository<ExamenParacliniqueType, Long> {

    @PreAuthorize("((#examenParacliniqueType.idExamenParaclinique == null or #examenParacliniqueType.idExamenParaclinique == 0) ? hasPermission(#examenParacliniqueType,'WRITE') : hasPermission(#examenParacliniqueType,'UPDATE'))")
    ExamenParacliniqueType save(ExamenParacliniqueType examenParacliniqueType);

    @RestResource(path = "byClient")
    Page<ExamenParacliniqueType> findAllByClientCreatorId(@Param("clientId") Long clientCreatorId, Pageable pageable);

    @RestResource(path = "byName")
    Page<ExamenParacliniqueType> findByClientCreatorIdAndNomExamenParacliniqueContainingIgnoreCase(
            @Param("clientId") Long clientCreatorId,@Param("name") String nomExamenParaclinique, Pageable pageable);

    @RestResource(path = "AllbyName")
    Page<ExamenParacliniqueType> findByNomExamenParacliniqueContainingIgnoreCase(String nomExamenParaclinique, Pageable pageable);

    @Override
    Optional<ExamenParacliniqueType> findById(Long aLong);

    @PreAuthorize("hasPermission(#id, 'ExamenParacliniqueType', 'DELETE')")
    @Override
    void deleteById(Long id);
}
