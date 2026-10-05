package com.znahodochka.backend.users.config.jwt.JwtKey;

import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtKeyStorageService {

    private final JwtKeyRepository jwtKeyRepository;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Transactional
    public SecretKey loadOrGenerateKey() {
        Optional<JwtKeyEntity> existingKey = jwtKeyRepository.findByKeyTypeAndIsActive(
                JwtKeyEntity.KeyType.HS512, true
        );

        if (existingKey.isPresent()) {
            return Keys.hmacShaKeyFor(existingKey.get().getKeyValue());
        }

        byte[] keyBytes = generateKeyBytes(64); // 512 бит = 64 байта
        validateKeyLength(keyBytes, "HS512");

        jwtKeyRepository.deactivateAllKeys(JwtKeyEntity.KeyType.HS512);

        JwtKeyEntity newKey = JwtKeyEntity.builder()
                .keyType(JwtKeyEntity.KeyType.HS512)
                .keyValue(keyBytes)
                .isActive(true)
                .build();

        JwtKeyEntity saved = jwtKeyRepository.save(newKey);

        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Transactional
    public SecretKey rotateKey() {
        log.warn("Forcing JWT key rotation...");

        jwtKeyRepository.deactivateAllKeys(JwtKeyEntity.KeyType.HS512);

        byte[] keyBytes = generateKeyBytes(64);
        JwtKeyEntity newKey = JwtKeyEntity.builder()
                .keyType(JwtKeyEntity.KeyType.HS512)
                .keyValue(keyBytes)
                .isActive(true)
                .build();

        JwtKeyEntity saved = jwtKeyRepository.save(newKey);

        return Keys.hmacShaKeyFor(keyBytes);
    }


    public boolean isKeyExists() {
        return jwtKeyRepository.findByKeyTypeAndIsActive(JwtKeyEntity.KeyType.HS512, true)
                .isPresent();
    }

    private byte[] generateKeyBytes(int length) {
        byte[] keyBytes = new byte[length];
        SECURE_RANDOM.nextBytes(keyBytes);
        return keyBytes;
    }

    private void validateKeyLength(byte[] keyBytes, String algorithm) {
        int requiredLength = switch (algorithm) {
            case "HS512" -> 64;
            case "HS384" -> 48;
            case "HS256" -> 32;
            default -> 32;
        };

        if (keyBytes.length < requiredLength) {
            throw new IllegalStateException(
                    String.format("%s requires key >= %d bytes, got: %d",
                            algorithm, requiredLength, keyBytes.length)
            );
        }
    }
}
