package com.simple_cabinet_medical.Backend.Dto;

public class ExistingFile {
    private String fileName;
    private String fileUrl;

    public ExistingFile(String fileName, String fileUrl) {
        this.fileName = fileName;
        this.fileUrl = fileUrl;
    }

    // Getters et Setters nécessaires pour la sérialisation JSON
    public String getFileName() { return fileName; }
    public String getFileUrl() { return fileUrl; }
}