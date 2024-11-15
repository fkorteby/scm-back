package com.simple_cabinet_medical.Backend.permisson;

import com.simple_cabinet_medical.Backend.model.Utilisateur;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Component
public class SecretairePermissionImpl implements IBasePermission {

    Logger logger = LoggerFactory.getLogger(SecretairePermissionImpl.class);

    @Override
    public boolean hasAccess(Object targetObject, User utilisateur) {
        return false;
    }
}
