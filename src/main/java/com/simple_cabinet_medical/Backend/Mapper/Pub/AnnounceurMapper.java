package com.simple_cabinet_medical.Backend.Mapper.Pub;

import com.simple_cabinet_medical.Backend.Dto.Pub.AnnonceurRequestDto;
import com.simple_cabinet_medical.Backend.Dto.Pub.AnnonceurResponseDto;
import com.simple_cabinet_medical.Backend.model.Annonceur;

public interface AnnounceurMapper {
    AnnonceurResponseDto EntityToDto(Annonceur annonceur);
    Annonceur DtoToEntity(AnnonceurRequestDto annonceurRequestDto);
}
