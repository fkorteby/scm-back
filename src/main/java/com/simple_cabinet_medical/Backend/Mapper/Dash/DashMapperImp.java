package com.simple_cabinet_medical.Backend.Mapper.Dash;

import com.simple_cabinet_medical.Backend.Dto.Dash.PatientAujhDto;
import com.simple_cabinet_medical.Backend.Dto.Dash.RenderVousAujhDto;
import com.simple_cabinet_medical.Backend.model.Consultation;
import com.simple_cabinet_medical.Backend.model.RendezVous;
import org.springframework.stereotype.Component;

@Component
public class DashMapperImp implements DashMapper {
    @Override
    public PatientAujhDto patientDtoFromConsultation(Consultation consultation) {
        return new PatientAujhDto(consultation.getPatient().getIdPatient(),
                consultation.getPatient().getNom(),
                consultation.getPatient().getPrenom(),
                consultation.getPatient().getDateNaissance(),
                consultation.getMotifConsultation(),
                consultation.getDateConsultation());
    }

    @Override
    public RenderVousAujhDto rendezVousDtoFromRendezVous(RendezVous rendezVous) {
        return new RenderVousAujhDto(rendezVous.getPatient().getIdPatient(),
                rendezVous.getPatient().getNom(),
                rendezVous.getPatient().getPrenom(),
                rendezVous.getPatient().getDateNaissance(),
                rendezVous.getHeureRendezVous(),
                rendezVous.getStatusRendezVous());

    }
}
