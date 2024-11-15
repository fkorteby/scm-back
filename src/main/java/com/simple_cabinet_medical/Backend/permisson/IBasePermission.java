package com.simple_cabinet_medical.Backend.permisson;

import com.simple_cabinet_medical.Backend.model.Utilisateur;
import org.springframework.security.core.userdetails.User;

public interface IBasePermission {

    // Evaluator the access to an object
    public boolean hasAccess(Object targetObject, User utilisateur);
}
