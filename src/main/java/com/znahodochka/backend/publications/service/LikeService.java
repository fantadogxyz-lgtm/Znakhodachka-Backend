package com.znahodochka.backend.publications.service;

import com.znahodochka.backend.users.dto.ShortUserInfoDto;

import java.util.List;
import java.util.UUID;

public interface LikeService {
    void like(UUID publicationId, Long userId);

    void unlike(UUID publicationId, Long userId);

    List<ShortUserInfoDto> getUsersWhoLiked(UUID publicationId);
}
