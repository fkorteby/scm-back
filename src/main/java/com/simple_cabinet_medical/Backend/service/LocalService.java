package com.simple_cabinet_medical.Backend.service;

import com.simple_cabinet_medical.Backend.controller.LocalController;
import com.simple_cabinet_medical.Backend.repository.LocalRepository;
import org.springframework.stereotype.Service;

@Service
public class LocalService {

    private final LocalRepository localRepository;

    public LocalService(LocalRepository localRepository) {
        this.localRepository = localRepository;
    }


}
