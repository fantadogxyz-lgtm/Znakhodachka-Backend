package com.znahodochka.backend.storage.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record DiscussionAttachmentDto(
        UUID id,
        String fileUri,
        Long photoOrder,
        Long type
) {
}
