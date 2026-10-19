package com.simple_cabinet_medical.Backend.Mapper.Client;

import com.simple_cabinet_medical.Backend.dto.ClientDto;
import com.simple_cabinet_medical.Backend.model.Client;

public interface ClientMapper {
    ClientDto EntityToDto(Client client);
}
