package com.znahodochka.backend.users.config.jwt;

import com.znahodochka.backend.users.config.jwt.JwtKey.JwtKeyStorageService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtKeyService {


    private final JwtKeyStorageService jwtKeyStorageService;

    private SecretKey hmacKey;

    @PostConstruct
    public void init() {
        try {
            this.hmacKey = jwtKeyStorageService.loadOrGenerateKey();
            log.info("JWT key initialized successfully from database");
        } catch (Exception e) {
            log.error("Failed to initialize JWT key from database", e);
            throw new IllegalStateException("Cannot initialize JWT key from database", e);
        }
    }

    public SecretKey getSigningKey() {
        return hmacKey;
    }

    public SecretKey rotateSigningKey() {
        this.hmacKey = jwtKeyStorageService.rotateKey();
        return hmacKey;
    }
}

