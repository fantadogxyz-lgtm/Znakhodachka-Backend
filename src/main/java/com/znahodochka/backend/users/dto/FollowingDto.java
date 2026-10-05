package com.znahodochka.backend.users.dto;

import lombok.Builder;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Builder
public record FollowingDto(UUID id, Long followerId, Long followingId, Instant createdAt) {
}
