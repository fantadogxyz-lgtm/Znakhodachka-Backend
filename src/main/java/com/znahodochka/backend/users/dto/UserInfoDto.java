package com.znahodochka.backend.users.dto;

import lombok.Builder;

import java.time.Instant;

@Builder
public record UserInfoDto(Long id, String login, String email, String name, String bio, String avatar_uri, Long numberOfFollowers, Long numberOfFollowing, Instant created_at) {
}
