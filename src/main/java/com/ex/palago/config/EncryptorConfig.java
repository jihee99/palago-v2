package com.ex.palago.config;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EncryptorConfig {

    @Value(value = "${jasypt.secret-key}")
    private String encryptorPassword;

    @Bean("propertyEncryptor")
    public StringEncryptor stringEncryptor() {
        PooledPBEStringEncryptor pooledPBEStringEncryptor = new PooledPBEStringEncryptor();

        SimpleStringPBEConfig simpleStringPBEConfig = new SimpleStringPBEConfig();
        simpleStringPBEConfig.setPassword(encryptorPassword);
        simpleStringPBEConfig.setAlgorithm("PBEWithMD5AndDES");
        simpleStringPBEConfig.setPoolSize(1);

        pooledPBEStringEncryptor.setConfig(simpleStringPBEConfig);
        return pooledPBEStringEncryptor;
    }
}
