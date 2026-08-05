package com.simple_cabinet_medical.Backend.repository;

import com.simple_cabinet_medical.Backend.model.ExamenClinique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(exported = false)
public interface ExamenCliniqueRepository extends JpaRepository<ExamenClinique, Long> {

    List<ExamenClinique> findAllByIdParentExamenClinique(@Param("idParentExamenClinique") Long idParentExamenClinique);
}
