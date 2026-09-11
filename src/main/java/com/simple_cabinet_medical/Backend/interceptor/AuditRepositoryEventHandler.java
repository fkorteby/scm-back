package com.simple_cabinet_medical.Backend.interceptor;

import org.springframework.data.rest.core.annotation.*;
import org.springframework.stereotype.Component;

@RepositoryEventHandler
@Component
public class AuditRepositoryEventHandler {

    @HandleBeforeCreate
    public void beforeCreate(Object entity) {
        // audit
    }

    @HandleAfterCreate
    public void afterCreate(Object entity) {
        // audit
    }

    @HandleBeforeSave
    public void beforeSave(Object entity) {
        // audit
    }

    @HandleAfterSave
    public void afterSave(Object entity) {
        // audit
    }

    @HandleBeforeDelete
    public void beforeDelete(Object entity) {
        // audit
    }

    @HandleAfterDelete
    public void afterDelete(Object entity) {
        // audit
    }
}