package com.znahodochka.backend.publications.dto;

import com.znahodochka.backend.publications.model.DiscussionType;
import com.znahodochka.backend.storage.dto.DiscussionAttachmentDto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record UpdateDiscussionDto(
        UUID id,
        String text,
        List<CreateGeolocationDto> geolocations

) {
}
