package com.znahodochka.backend.publications.dto;

import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record FeedDiscussionResponse(
        List<DiscussionDto> items,
        Instant nextCursor,
        boolean hasMore
) {
}
