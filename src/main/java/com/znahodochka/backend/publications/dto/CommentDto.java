package com.znahodochka.backend.publications.dto;

import com.znahodochka.backend.users.dto.ShortUserInfoDto;
import lombok.Builder;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Builder(toBuilder = true)
public record CommentDto(
        UUID id,
        UUID publicationId,
        UUID parentId,
        String content,
        Instant createdAt,
        ShortUserInfoDto author,
        ShortUserInfoDto mentionedUser,
        Long repliesCount,
        List<CommentDto> replies
) {
}
