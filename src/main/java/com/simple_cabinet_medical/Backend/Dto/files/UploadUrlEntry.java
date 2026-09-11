package com.simple_cabinet_medical.Backend.Dto.files;

public class UploadUrlEntry {
    private String fileName;
    private String uploadUrl;

    public UploadUrlEntry() {}

    public UploadUrlEntry(String fileName, String uploadUrl) {
        this.fileName = fileName;
        this.uploadUrl = uploadUrl;
    }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getUploadUrl() { return uploadUrl; }
    public void setUploadUrl(String uploadUrl) { this.uploadUrl = uploadUrl; }
}