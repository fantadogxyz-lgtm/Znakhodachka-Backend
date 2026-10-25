package com.znahodochka.backend.publications.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "communities", schema = "publications")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Community {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String slug;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "cover_uri")
    private String coverUri;

    @Column(name = "creator_id", nullable = false)
    private Long creatorId;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "members_count ")
    private Long membersCount;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
    }
}
