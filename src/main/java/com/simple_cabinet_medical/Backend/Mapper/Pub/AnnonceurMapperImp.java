package com.simple_cabinet_medical.Backend.Mapper.Pub;

import com.simple_cabinet_medical.Backend.Dto.Pub.AnnonceurRequestDto;
import com.simple_cabinet_medical.Backend.Dto.Pub.AnnonceurResponseDto;
import com.simple_cabinet_medical.Backend.model.Annonceur;
import com.simple_cabinet_medical.Backend.model.EStatusAnnonceur;
import org.springframework.stereotype.Component;

@Component
public class AnnonceurMapperImp implements AnnounceurMapper {

    @Override
    public AnnonceurResponseDto EntityToDto(Annonceur annonceur) {
        if (annonceur == null) {
            return null;
        }

        AnnonceurResponseDto dto = new AnnonceurResponseDto();

        // Convert Long ID to String
        if (annonceur.getIdAnnonceur() != null) {
            dto.setIdAnnonceur(String.valueOf(annonceur.getIdAnnonceur()));
        }

        dto.setNomAnnonceur(annonceur.getNomAnnonceur());
        dto.setRaisonSociale(annonceur.getRaisonSociale());
        dto.setNumeroRegistreCommerce(annonceur.getNumeroRegistreCommerce());
        dto.setNif(annonceur.getNif());
        dto.setNis(annonceur.getNis());
        dto.setRib(annonceur.getRib());
        dto.setAdresseLegale(annonceur.getAdresseLegale());
        dto.setTypeAnnonceur(annonceur.getTypeAnnonceur());
        dto.setSecteurActivite(annonceur.getSecteurActivite());
        dto.setStatus(annonceur.getStatus());
        dto.setNomContatct(annonceur.getUtilisateur().getNom());
        dto.setPrenomContatct(annonceur.getUtilisateur().getPrenom());
        dto.setEmailContact(annonceur.getUtilisateur().getEmail());
        dto.setTelephoneContact(annonceur.getTelephoneContact());

        return dto;
    }

    @Override
    public Annonceur DtoToEntity(AnnonceurRequestDto annonceurRequestDto) {
        if (annonceurRequestDto == null) {
            return null;
        }

        Annonceur entity = new Annonceur();

        entity.setNomAnnonceur(annonceurRequestDto.getNomAnnonceur());
        entity.setRaisonSociale(annonceurRequestDto.getRaisonSociale());
        entity.setNumeroRegistreCommerce(annonceurRequestDto.getNumeroRegistreCommerce());
        entity.setNif(annonceurRequestDto.getNif());
        entity.setNis(annonceurRequestDto.getNis());
        entity.setRib(annonceurRequestDto.getRib());
        entity.setAdresseLegale(annonceurRequestDto.getAdresseLegale());
        entity.setTypeAnnonceur(annonceurRequestDto.getTypeAnnonceur());
        entity.setSecteurActivite(annonceurRequestDto.getSecteurActivite());
        entity.setStatus(EStatusAnnonceur.En_attente);
        entity.setTelephoneContact(annonceurRequestDto.getTelephoneContact());

        return entity;
    }
}