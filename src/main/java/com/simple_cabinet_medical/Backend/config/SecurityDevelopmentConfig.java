package com.simple_cabinet_medical.Backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
@Profile("development")
public class SecurityDevelopmentConfig {

    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest().permitAll()
                .and().csrf(csrf -> csrf.disable());
    }
}
