package com.znahodochka.backend.users.config.jwt.JwtKey;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface JwtKeyRepository extends JpaRepository<JwtKeyEntity, Long> {
    Optional<JwtKeyEntity> findByKeyTypeAndIsActive(JwtKeyEntity.KeyType keyType, Boolean isActive);

    @Modifying
    @Transactional
    @Query("UPDATE JwtKeyEntity j SET j.isActive = false WHERE j.keyType = :keyType")
    void deactivateAllKeys(JwtKeyEntity.KeyType keyType);
}
