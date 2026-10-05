package com.znahodochka.backend.publications.dto;

import com.znahodochka.backend.storage.dto.UpdateAttachmentDto;
import lombok.Builder;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Builder
public record UpdatePublicationDto(
        String title,
        String description,
        Long categoryId,
        Set<Long> tagIds,
        Instant endDate
) {}
