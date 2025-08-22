package com.simple_cabinet_medical.Backend.Mapper.Patient;

import com.simple_cabinet_medical.Backend.Dto.PatientResponseDto;
import com.simple_cabinet_medical.Backend.model.Patient;
import org.springframework.stereotype.Component;

@Component
public class PatientMapperImp implements PatientMapper {
    @Override
    public PatientResponseDto DtoFromEntity(Patient patient) {
        return new PatientResponseDto(
                patient.getIdPatient(),
                patient.getNom(),
                patient.getPrenom(),
                patient.getNumeroTel()
        );
    }
}
