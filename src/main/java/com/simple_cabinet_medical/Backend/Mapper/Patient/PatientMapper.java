package com.simple_cabinet_medical.Backend.Mapper.Patient;

import com.simple_cabinet_medical.Backend.Dto.PatientResponseDto;
import com.simple_cabinet_medical.Backend.model.Patient;

public interface PatientMapper {
     PatientResponseDto DtoFromEntity(Patient patient);
}
