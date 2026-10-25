package com.znahodochka.backend.publications.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "community_participation", schema = "publications")
public class CommunityParticipation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "community_id", nullable = false)
    private UUID communityId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "joined_at")
    private Instant joinedAt;

    @PrePersist
    protected void onCreate() {
        if (joinedAt == null) joinedAt = Instant.now();
    }
}