package com.znahodochka.backend.storage.dto;

import java.util.UUID;

public record UpdateDiscussionAttachmentDto(
        UUID id,
        UUID attachmentId,
        Long order
) {
}
