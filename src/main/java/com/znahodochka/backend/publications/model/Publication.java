package com.znahodochka.backend.publications.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "publications", schema = "publications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Publication {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "author_id", nullable = false)
    private Long authorId;

    @Column(name = "publication_type", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private PublicationType publicationType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    private String title;

    @Column(length = 2000)
    private String description;

    private Double latitude;
    private Double longitude;

    private String region;
    private String city;

    @Column(name = "likes_count")
    private Long likesCount;

    @Column(name = "comments_count")
    private Long commentsCount;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "end_date")
    private Instant endDate;

    @JoinColumn(name = "parent_publication_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Publication parentPublication;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "publication_tags",
            schema = "publications",
            joinColumns = @JoinColumn(name = "publication_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
        if (likesCount == null) likesCount = 0L;
        if (commentsCount == null) commentsCount = 0L;
    }
}
