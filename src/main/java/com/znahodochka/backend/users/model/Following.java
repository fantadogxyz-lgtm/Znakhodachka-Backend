package com.znahodochka.backend.users.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "following", schema = "users")
public class Following {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name ="follower_id")
    private Long followerId;
    @Column(name = "follow_id")
    private Long followingId;
    @Column(name = "created_at")
    private Instant followDate;
}
