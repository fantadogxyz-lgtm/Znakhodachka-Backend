package com.znahodochka.backend.publications.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "discussion_likes", schema = "publications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscussionLike {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "author_id", nullable = false)
    private Long authorId;

    @JoinColumn(name = "discussion_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Discussion discussion;

    @Column(name = "created_at")
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
    }
}
