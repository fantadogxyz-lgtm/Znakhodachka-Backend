package com.znahodochka.backend.publications.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "discussions", schema = "publications")
@Builder(toBuilder = true)
public class
Discussion {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "text", nullable = false)
    String text;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    DiscussionType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    Discussion parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    Discussion topic;

    @Column(name = "author_id", nullable = false)
    Long authorId;

    @Column(name = "created_at")
    private Instant createdAt;


    @Column(name = "count_of_likes", nullable = false)
    Long countOfLikes;

    @ManyToOne
    @JoinColumn(name = "community_id")
    Community community;

    @Column(name = "mentioned_user_id")
    private Long mentionedUserId;


    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
        if (countOfLikes == null) countOfLikes = 0L;
    }
}
