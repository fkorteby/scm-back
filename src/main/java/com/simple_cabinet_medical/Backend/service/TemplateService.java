package com.simple_cabinet_medical.Backend.service;

import com.simple_cabinet_medical.Backend.repository.TemplateRepository;
import org.springframework.stereotype.Service;

@Service
public class TemplateService {

    private final TemplateRepository templateRepository;

    public TemplateService(TemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    public void addTemplate () {}

    public void deleteTemplate () {}

    public void updateTemplate () {}
}
