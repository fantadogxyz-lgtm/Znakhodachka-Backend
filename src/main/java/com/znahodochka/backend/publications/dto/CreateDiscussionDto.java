package com.znahodochka.backend.publications.dto;

import com.znahodochka.backend.publications.model.DiscussionType;
import com.znahodochka.backend.storage.dto.DiscussionAttachmentDto;
import jakarta.annotation.Nullable;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CreateDiscussionDto(
        String text,
        DiscussionType type,
        UUID parent_id,
        UUID topic_id,
        @Nullable Long mentionedUserId,
        List<DiscussionAttachmentDto>  attachments,
        List<CreateGeolocationDto> geolocations
        ) {
}
