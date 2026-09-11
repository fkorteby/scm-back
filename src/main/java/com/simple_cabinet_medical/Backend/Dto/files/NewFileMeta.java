package com.simple_cabinet_medical.Backend.Dto.files;

public class NewFileMeta {
    private String fileName;
    private String contentType;

    public NewFileMeta() {}

    public NewFileMeta(String fileName, String contentType) {
        this.fileName = fileName;
        this.contentType = contentType;
    }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
}
