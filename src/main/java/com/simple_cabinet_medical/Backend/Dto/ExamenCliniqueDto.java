package com.simple_cabinet_medical.Backend.Dto;

import java.util.Objects;
import java.util.Set;

public class ExamenCliniqueDto {
    private Long idExamenClinique;
    private String nomExamenClinique;
    private Long idParentExamenClinique;
    private Set<ExamenCliniqueDto> examenCliniqueDto;

    public ExamenCliniqueDto(Long idExamenClinique, String nomExamenClinique, Long idParentExamenClinique, Set<ExamenCliniqueDto> examenCliniqueDto) {
        this.idExamenClinique = idExamenClinique;
        this.nomExamenClinique = nomExamenClinique;
        this.idParentExamenClinique = idParentExamenClinique;
        this.examenCliniqueDto = examenCliniqueDto;
    }

    public ExamenCliniqueDto() {

    }

    public Long getIdExamenClinique() {
        return idExamenClinique;
    }

    public void setIdExamenClinique(Long idExamenClinque) {
        this.idExamenClinique = idExamenClinque;
    }

    public String getNomExamenClinique() {
        return nomExamenClinique;
    }

    public void setNomExamenClinique(String nomExamenClinque) {
        this.nomExamenClinique = nomExamenClinque;
    }

    public Long getIdParentExamenClinique() {
        return idParentExamenClinique;
    }

    public void setIdParentExamenClinique(Long idParentExamenClinique) {
        this.idParentExamenClinique = idParentExamenClinique;
    }

    public Set<ExamenCliniqueDto> getExamenCliniqueDto() {
        return examenCliniqueDto;
    }

    public void setExamenCliniqueDto(Set<ExamenCliniqueDto> examenCliniqueDto) {
        this.examenCliniqueDto = examenCliniqueDto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExamenCliniqueDto)) return false;
        ExamenCliniqueDto that = (ExamenCliniqueDto) o;
        return Objects.equals(idExamenClinique, that.idExamenClinique);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idExamenClinique);
    }
}
