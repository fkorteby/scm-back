package com.simple_cabinet_medical.Backend.utils;

import com.simple_cabinet_medical.Backend.model.BasedObject;
import jakarta.persistence.PrePersist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BasedObjectListener {

    @Autowired
    private SpringSecurityAuditorAware auditorAware;

    @PrePersist
    public void setClientCreatorId(BasedObject entity) {
        if (entity.getClientCreatorId() == null) {
            auditorAware.getCurrentClientId().ifPresent(entity::setClientCreatorId);
        }
    }
}
