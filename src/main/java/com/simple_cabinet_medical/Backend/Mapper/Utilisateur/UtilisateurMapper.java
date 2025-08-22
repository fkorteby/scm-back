package com.simple_cabinet_medical.Backend.Mapper.Utilisateur;

import com.simple_cabinet_medical.Backend.Dto.UtilisateurDto;
import com.simple_cabinet_medical.Backend.model.Utilisateur;

public interface UtilisateurMapper {
    UtilisateurDto EntityToDto(Utilisateur utilisateur);
}
