package com.znahodochka.backend.publications.dto;

import com.znahodochka.backend.publications.model.DiscussionType;
import com.znahodochka.backend.storage.dto.AttachmentDto;
import com.znahodochka.backend.storage.dto.DiscussionAttachmentDto;
import com.znahodochka.backend.users.dto.ShortUserInfoDto;
import lombok.Builder;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Builder
public record DiscussionDto(
        UUID id,
        String text,
        DiscussionType type,
        UUID parent_id,
        UUID topic_id,
        Long author_id,
        List<DiscussionAttachmentDto> attachments,
        ShortUserInfoDto mentionedUser,
        Instant created_at,
        Long countOfLikes,
        Boolean isAuthor,
        UUID community_id,
        Boolean isLiked

) {
}
