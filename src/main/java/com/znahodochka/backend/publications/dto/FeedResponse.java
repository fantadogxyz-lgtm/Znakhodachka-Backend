package com.znahodochka.backend.publications.dto;

import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record FeedResponse(
        List<PublicationDto> items,
        Instant nextCursor,
        boolean hasMore
) {
}
