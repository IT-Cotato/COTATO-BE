package org.cotato.csquiz.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;

@Configuration
public class EncodeConfig {

    @Value("${aes.secret.key}")
    String aesKey;

    @Value("${aes.secret.salt}")
    String salt;

    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AesBytesEncryptor encryptor() {
        return new AesBytesEncryptor(aesKey, salt);
    }
}
