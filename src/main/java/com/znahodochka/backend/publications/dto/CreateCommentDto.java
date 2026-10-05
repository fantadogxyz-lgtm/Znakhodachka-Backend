package com.znahodochka.backend.publications.dto;

import jakarta.annotation.Nullable;
import lombok.Builder;

import java.util.UUID;

@Builder
public record CreateCommentDto(
        String content,
        @Nullable UUID parentId,
        @Nullable Long mentionedUserId
) {
}
