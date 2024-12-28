package com.simple_cabinet_medical.Backend.controller;

import com.simple_cabinet_medical.Backend.service.TemplateService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TemplateController {

    private final TemplateService templateService;

    public TemplateController(TemplateService templateService) {
        this.templateService = templateService;
    }

}
