package com.ex.palago.config;

import com.ex.palago.security.jwt.JwtProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties({
        JwtProperties.class
})
@Configuration
public class ConfigurationPropertiesConfig {}
