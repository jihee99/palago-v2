package com.ex.palago.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Getter
@AllArgsConstructor
@ConstructorBinding
@ConfigurationProperties(prefix = "auth.jwt")
public class JwtProperties {
    private String secretKey;
    private String prefix;
    private String headerString;
    private int expirationTime;

//    public JwtProperties(@Value("${auth.jwt.secret}") String secretKey,
//                         @Value("${auth.jwt.prefix}") String prefix,
//                         @Value("${auth.jwt.header-string}") String headerString,
//                         @Value("${auth.jwt.expiration-time}") int expirationTime) {
//        this.secretKey = secretKey;
//        this.prefix = prefix;
//        this.headerString = headerString;
//        this.expirationTime = expirationTime;
//    }
}

//public interface JwtProperties {
//    String SECRET = "조익현"; // 우리 서버만 알고 있는 비밀값
//    int EXPIRATION_TIME = 864000000; // 10일 (1/1000초)
//    String TOKEN_PREFIX = "Bearer ";
//    String HEADER_STRING = "Authorization";
//}