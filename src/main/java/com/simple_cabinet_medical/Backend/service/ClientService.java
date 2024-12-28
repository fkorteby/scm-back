package com.simple_cabinet_medical.Backend.service;

import com.simple_cabinet_medical.Backend.repository.ClientRepository;
import org.springframework.stereotype.Service;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public void addClient () {}

    public void updateClient () {}

    public void deleteClient () {}

    public void getAllClient () {}
}
