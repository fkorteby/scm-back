package com.simple_cabinet_medical.Backend.permisson;

import com.simple_cabinet_medical.Backend.model.Patient;
import com.simple_cabinet_medical.Backend.model.Utilisateur;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Optional;

@Component
public class MedecinPermissionImpl implements IBasePermission{

    Logger logger = LoggerFactory.getLogger(MedecinPermissionImpl.class);

    @Override
    public boolean hasAccess (Object targetObject, User user) {

        String finalClassName;
        Object finalObject;

        // For primitive type
        if (targetObject.getClass().isPrimitive() || targetObject instanceof byte[] || targetObject instanceof String) {
            // no right for primitive type
            // we don't have enough informations to allow/block
            return true;

            // For Optional type
        } else if (targetObject instanceof Optional) {
            finalClassName = ((Optional<?>) targetObject).get().getClass().getSimpleName();
            finalObject = ((Optional) targetObject).get();

            // For Page type
        } else if (targetObject instanceof Page) {
            // Check just the first element
            if (((Page<?>) targetObject).getContent() != null && ((Page<?>) targetObject).getContent().size() > 0) {
                finalClassName = ((Page<?>) targetObject).getContent().get(0).getClass().getSimpleName();
                finalObject = ((Page) targetObject).getContent().get(0);
            } else {
                // page is empty return true
                return true;
            }

            // For Timestamp type (returned by lastUpdate)
        } else if (targetObject instanceof Timestamp) {
            return true;

            // For Integer type (returned by activatingSleepMode in kiosk)
        } else if (targetObject instanceof Integer) {
            return true;
        }

        // For remaining types
        else {
            finalClassName = targetObject.getClass().getSimpleName();
            finalObject = targetObject;
        }

        // Check access for Product instance
        try {

            switch (BO_ENUM.getEnum(finalClassName)) {

                case Patient:
                    if (user.getAuthorities().contains(new SimpleGrantedAuthority(
                            Utils.CLIENT_AUTH_PREFIX + ((Patient) finalObject).getUtilisateur().getClient()))) {
                        return true;
                    }
                    break;


                case ObjectArray:
                    if (user.getAuthorities().contains(new SimpleGrantedAuthority(
                            Utils.CLIENT_AUTH_PREFIX + ((Object[]) finalObject)[0].toString()))) {
                        return true;
                    }
                    break;
                default:
                    break;
            }

        } catch (NullPointerException e) {
            // Case of save
            return true;

        } catch (Exception ex) {
            // log the error
            logger.error(ex.toString());
        }

        // else deny access
        return false;
    }
}
