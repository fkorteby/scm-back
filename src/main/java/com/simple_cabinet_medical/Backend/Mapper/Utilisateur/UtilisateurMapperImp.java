package com.simple_cabinet_medical.Backend.Mapper.Utilisateur;

import com.simple_cabinet_medical.Backend.Dto.UtilisateurDto;
import com.simple_cabinet_medical.Backend.model.Utilisateur;
import org.springframework.stereotype.Component;

@Component
public class UtilisateurMapperImp implements UtilisateurMapper{
    public UtilisateurDto EntityToDto(Utilisateur utilisateur) {
        UtilisateurDto utilisateurDto = new UtilisateurDto(
                utilisateur.getIdUtilisateur(),
                utilisateur.getNom(),
                utilisateur.getPrenom(),
                utilisateur.getNomUtilisateur(),
                utilisateur.getRole(),
                utilisateur.getClient() != null ? utilisateur.getClient().getNomClient() : null
        );
        utilisateurDto.setIdUtilisateur(utilisateur.getIdUtilisateur());
        utilisateurDto.setNom(utilisateur.getNom());
        utilisateurDto.setPrenom(utilisateur.getPrenom());
        utilisateurDto.setNomUtilisateur(utilisateur.getNomUtilisateur());
        utilisateurDto.setRole(utilisateur.getRole());
        utilisateurDto.setClientNom(utilisateur.getClient().getNomClient());

        if (utilisateur.getClient() != null) {
            utilisateurDto.setClientNom(utilisateur.getClient().getNomClient());
        }

        return utilisateurDto;
    }
}
