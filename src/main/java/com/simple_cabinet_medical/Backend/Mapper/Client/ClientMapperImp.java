package com.simple_cabinet_medical.Backend.Mapper.Client;

import com.simple_cabinet_medical.Backend.Dto.ClientDto;
import com.simple_cabinet_medical.Backend.model.Client;
import org.springframework.stereotype.Component;

@Component
public class ClientMapperImp implements ClientMapper {
    @Override
    public ClientDto EntityToDto(Client client) {
        ClientDto clientDto = new ClientDto();
        clientDto.setIdClient(client.getIdClient());
        clientDto.setNomClient(client.getNomClient());
        clientDto.setAdresse(client.getAdresse());
        return clientDto;
    }
}
