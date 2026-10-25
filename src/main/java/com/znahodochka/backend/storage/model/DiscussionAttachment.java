package com.znahodochka.backend.storage.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "discussion_attachment", schema = "storage")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscussionAttachment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "discussion_id")
    private UUID discussionId;

    @Column(name = "type")
    private Long type;

    @Column(name = "file_uri")
    private String fileUri;

    @Column(name = "photo_order")
    private Long photoOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bucket_id")
    private Bucket bucket;

    @Column(name = "created_at")
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
    }
}
