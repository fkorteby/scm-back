package com.simple_cabinet_medical.Backend.Dto.files;


import java.util.List;

public class UpdateFilesRequest {
    private List<String> deletedFileNames;
    private List<NewFileMeta> newFilesMeta;

    public List<String> getDeletedFileNames() { return deletedFileNames; }
    public void setDeletedFileNames(List<String> deletedFileNames) { this.deletedFileNames = deletedFileNames; }

    public List<NewFileMeta> getNewFilesMeta() { return newFilesMeta; }
    public void setNewFilesMeta(List<NewFileMeta> newFilesMeta) { this.newFilesMeta = newFilesMeta; }
}