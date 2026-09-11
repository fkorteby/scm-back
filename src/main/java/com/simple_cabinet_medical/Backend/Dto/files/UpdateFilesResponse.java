package com.simple_cabinet_medical.Backend.Dto.files;

import java.util.List;

public class UpdateFilesResponse {
    private List<UploadUrlEntry> uploadUrls;

    public UpdateFilesResponse() {}

    public UpdateFilesResponse(List<UploadUrlEntry> uploadUrls) {
        this.uploadUrls = uploadUrls;
    }

    public List<UploadUrlEntry> getUploadUrls() { return uploadUrls; }
    public void setUploadUrls(List<UploadUrlEntry> uploadUrls) { this.uploadUrls = uploadUrls; }
}
