package com.simple_cabinet_medical.Backend.service;

import com.simple_cabinet_medical.Backend.repository.TemplateRepository;
import org.springframework.stereotype.Service;

@Service
public class TemplateService {

    private final TemplateRepository templateRepository;
    private final AccessControlService accessControlService;

    public TemplateService(TemplateRepository templateRepository, AccessControlService accessControlService) {
        this.templateRepository = templateRepository;
        this.accessControlService = accessControlService;
    }

    public void addTemplate () {}

    public void deleteTemplate () {}

    public void updateTemplate () {}
}
