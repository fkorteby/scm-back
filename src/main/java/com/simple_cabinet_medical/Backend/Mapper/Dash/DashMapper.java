package com.simple_cabinet_medical.Backend.Mapper.Dash;

import com.simple_cabinet_medical.Backend.Dto.Dash.PatientAujhDto;
import com.simple_cabinet_medical.Backend.Dto.Dash.RenderVousAujhDto;
import com.simple_cabinet_medical.Backend.model.Consultation;
import com.simple_cabinet_medical.Backend.model.RendezVous;

public interface DashMapper {

   PatientAujhDto patientDtoFromConsultation(Consultation consultation);
   RenderVousAujhDto rendezVousDtoFromRendezVous(RendezVous rendezVous);
}
