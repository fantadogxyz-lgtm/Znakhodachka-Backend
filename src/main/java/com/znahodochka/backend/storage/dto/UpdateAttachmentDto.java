package com.znahodochka.backend.storage.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UpdateAttachmentDto(
        UUID id,
        UUID attachmentId,
        Long order
) {}
