package com.znahodochka.backend.publications.dto;

import com.znahodochka.backend.publications.model.PublicationType;
import com.znahodochka.backend.storage.dto.AttachmentDto;
import com.znahodochka.backend.users.dto.ShortUserInfoDto;
import jakarta.annotation.Nullable;
import lombok.Builder;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Builder(toBuilder = true)
public record PublicationDto (
    UUID id,
    PublicationType type,
    Boolean isLiked,
    CategoryDto category,
    Set<TagDto> tags,
    CommunityShortDto communityShortDto,
    Set<CommunityTagDto> communityTags,
    String title,
    String description,
    Double latitude,
    Double longitude,
    String region,
    String city,
    Boolean isChallengeActive,
    Boolean isWinner,
    ShortUserInfoDto shortUserInfoDto,
    UUID parentPublicationId,
    List<AttachmentDto> attachments,
    Long likesCount,
    Long commentsCount,
    Instant createdAt,
    @Nullable Instant endDate) {
}
