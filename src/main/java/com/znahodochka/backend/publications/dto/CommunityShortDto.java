package com.znahodochka.backend.publications.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CommunityShortDto(
        UUID id,
        String slug,
        String title,
        String coverUri,
        Long memberCount,
        boolean isOwner
) {}