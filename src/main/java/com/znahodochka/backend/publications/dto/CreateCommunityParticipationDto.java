package com.znahodochka.backend.publications.dto;

import java.util.UUID;

public record CreateCommunityParticipationDto(
        Long userId,
        UUID communityId
) {
}
