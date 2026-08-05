package com.simple_cabinet_medical.Backend.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(GcsProperties.class)
public class GcsConfiguration {
}
