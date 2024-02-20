package com.ex.palago.security.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PalagoSecretHolder implements SecretHolder {

    @Value("${auth.jwt.secret-key}")
    private String SECRET;

    @Override
    public String getSecret() {
        return SECRET;
    }
}
