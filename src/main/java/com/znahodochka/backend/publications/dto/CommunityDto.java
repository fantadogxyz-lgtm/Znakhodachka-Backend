package com.znahodochka.backend.publications.dto;

import com.znahodochka.backend.users.dto.ShortUserInfoDto;
import lombok.Builder;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Builder
public record CommunityDto(
        UUID id,
        String slug,
        String title,
        String description,
        String coverUri,
        ShortUserInfoDto creator,
        Long membersCount,
        List<CommunityTagDto> tags,
        boolean isMember,
        boolean isCreator,
        Instant createdAt
) {}