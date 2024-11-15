package com.simple_cabinet_medical.Backend.permisson;

import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.stereotype.Component;

import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Component
public class BaseMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {

    @Autowired
    BasePermissionElevator basePermissionEvaluator;

    @Override
    public Object filter(Object filterTarget, Expression filterExpression, EvaluationContext ctx) {

        if(ctx.getRootObject().getValue() instanceof SecurityExpressionRoot) {
            // Force it to use our custom BasePermissionEvaluator
            logger.debug("Filtering with expression with BasePermissionEvaluator " );
            ((SecurityExpressionRoot) ctx.getRootObject().getValue()).setPermissionEvaluator(basePermissionEvaluator);
        }

        if(filterTarget instanceof Collection || filterTarget.getClass().isArray()) {

            // normal behavior
            return super.filter(filterTarget, filterExpression, ctx);

        } else {

            try{
                User user = (User) ((SecurityExpressionRoot)ctx.getRootObject().getValue()).getAuthentication().getPrincipal();

                if(basePermissionEvaluator.hasAccess(filterTarget, user)) {
                    return filterTarget;
                } else {
                    return null;
                }
            } catch(Exception ex) {
                logger.debug(ex.getMessage());
                return null;
            }

        }

    }
}
