package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.model.CertificatType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource
@PostFilter("hasPermission(filterObject,'READ')")
public interface CertificatTypeRepository extends JpaRepository<CertificatType, Long> {

    @PreAuthorize("((#certificatType.idCertificat == null or #certificatType.idCertificat == 0)" +
            " ? hasPermission(#certificatType,'WRITE') : hasPermission(#certificatType,'UPDATE'))")
    CertificatType save(CertificatType certificatType);

    @RestResource(path = "byClient")
    Page<CertificatType> findAllByClientCreatorId(@Param("clientId") Long clientCreatorId, Pageable pageable);

    @RestResource(path = "by-Client")
    List<CertificatType> findAllByClientCreatorId(@Param("clientId") Long clientCreatorId);

    @RestResource(path = "byName")
    Page<CertificatType> findByClientCreatorIdAndNomCertificatContainingIgnoreCase(@Param("clientId") Long clientCreatorId,
                                                                                   String name,
                                                                                   Pageable pageable);

    @RestResource(path = "AllbyName")
    Page<CertificatType> findByNomCertificatContainingIgnoreCase(String name, Pageable pageable);

    @Override
    Optional<CertificatType> findById(Long aLong);

    @PreAuthorize("hasPermission(#id, 'CertificatType', 'DELETE')")
    @Override
    void deleteById(Long id);
}
