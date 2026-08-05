package com.simple_cabinet_medical.Backend.controller;

import com.simple_cabinet_medical.Backend.service.ExcelTemplateService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/template")
public class ExcelTemplateController {

    private final ExcelTemplateService excelTemplateService;

    public ExcelTemplateController(ExcelTemplateService excelTemplateService) {
        this.excelTemplateService = excelTemplateService;
    }

    @GetMapping("/download/{type}")
    public ResponseEntity<byte[]> downloadTemplate(@PathVariable String type) {
        byte[] excelFile = excelTemplateService.generateTemplate(type);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + type + "_Template.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelFile);
    }
    @PostMapping("/import/{type}")
    public ResponseEntity<String> importExcel(
            @PathVariable String type,
            @RequestParam("file") MultipartFile file) {

        excelTemplateService.importExcelData(type, file);
        return ResponseEntity.ok("Data imported successfully for type: " + type);
    }
}
