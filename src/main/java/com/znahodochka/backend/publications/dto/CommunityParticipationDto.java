package com.znahodochka.backend.publications.dto;

import java.time.Instant;
import java.util.UUID;

public record CommunityParticipationDto(
        UUID id,
        Long userId,
        UUID communityId,
        Instant joinedAt
) {
}
