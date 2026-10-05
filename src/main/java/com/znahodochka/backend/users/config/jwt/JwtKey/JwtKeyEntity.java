package com.znahodochka.backend.users.config.jwt.JwtKey;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "jwt_keys", schema = "server_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtKeyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "key_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private KeyType keyType;

    @Column(name = "key_value", nullable = false, columnDefinition = "BYTEA")
    private byte[] keyValue;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    public enum KeyType {
        HS512, HS384, HS256
    }
}
