package com.znahodochka.backend.publications.dto;

import com.znahodochka.backend.publications.model.PublicationType;
import jakarta.annotation.Nullable;
import lombok.Builder;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Builder
public record CreatePublicationDto(
        PublicationType type,
        Long category,
        Set<Long> tags,
        String title,
        String description,
        Double latitude,
        Double longitude,
        String region,
        String city,
        UUID parentPublicationId,
        List<UUID>attachmentIds,
        Instant endDate){
}
