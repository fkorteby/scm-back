package com.simple_cabinet_medical.Backend.permisson;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class BaseMethodSecurityConfig extends GlobalMethodSecurityConfiguration {

    @Bean
    public MethodSecurityExpressionHandler expressionHandler() {
        final BaseMethodSecurityExpressionHandler handler = new BaseMethodSecurityExpressionHandler();
        handler.setPermissionEvaluator(new BasePermissionElevator());
        return handler;
    }

    // this causes an IllegalArgumentException ("A ServletContext is required to configure default servlet handling")
    @Bean
    public PermissionEvaluator permissionEvaluator() {
        BasePermissionElevator bean = new BasePermissionElevator();
        return bean;
    }


    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        // final DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        final BaseMethodSecurityExpressionHandler handler = new BaseMethodSecurityExpressionHandler();
        handler.setPermissionEvaluator(new BasePermissionElevator());
        return handler;
    }
}
