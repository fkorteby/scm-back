package com.simple_cabinet_medical.Backend.config;

import com.simple_cabinet_medical.Backend.Permission.BaseMethodSecurityExpressionHandler;
import com.simple_cabinet_medical.Backend.Permission.CustomPermissionEvaluator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                // VOTRE CONFIGURATION CORS DOIT VENIR EN PREMIER
                .cors(cors -> cors.configurationSource(request -> {
                    var corsConfig = new org.springframework.web.cors.CorsConfiguration();
                    corsConfig.setAllowedOriginPatterns(List.of(
                            "http://localhost:4200",
                            "https://app.simple-cabinet-medical.com",
                            "https://vps159368.vps.ovh.ca"
                    ));
                    corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
                    corsConfig.setAllowedHeaders(List.of("*"));
                    corsConfig.setAllowCredentials(true);
                    return corsConfig;
                }))
                .authorizeHttpRequests(request -> request
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                "/uploads/**",
                                "/api/pub/media/**",
                                "/api/v1/auth/login",
                                "/api/v1/auth/reset-password",
                                "/api/v1/clients/register",
                                "/api/v1/annonceurs/register",
                                "/api/v1/clients/checkClientOtp",
                                "/api/test/**",
                                "/api/v1/annonceurs/checkOtp",
                                "/api/v1/clients/generateOtp",
                                "/api/v1/annonceurs/regenerateOtp",
                                "/v2/api-docs",
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/swagger-resources",
                                "/swagger-resources/**",
                                "/configuration/ui",
                                "/configuration/security",
                                "/swagger-ui/**",
                                "/webjars/**",
                                "/swagger-ui.html",
                                "/actuator/**",
                                "/hawtio/**",      // ← add this
                                "/jolokia/**"
                        ).permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    @Primary
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler(CustomPermissionEvaluator permissionEvaluator) {
        BaseMethodSecurityExpressionHandler handler = new BaseMethodSecurityExpressionHandler();
        handler.setPermissionEvaluator(permissionEvaluator);
        return handler;
    }

}
