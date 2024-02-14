package com.ex.palago.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Getter
@Component
public class JwtProperties {
    private final String secretKey;
    private final String prefix;
    private final String headerString;
    private final int expirationTime;

    public JwtProperties(@Value("${auth.jwt.secret}") String secretKey,
                         @Value("${auth.jwt.prefix}") String prefix,
                         @Value("${auth.jwt.header-string}") String headerString,
                         @Value("${auth.jwt.expiration-time}") int expirationTime) {
        this.secretKey = secretKey;
        this.prefix = prefix;
        this.headerString = headerString;
        this.expirationTime = expirationTime;
    }
}