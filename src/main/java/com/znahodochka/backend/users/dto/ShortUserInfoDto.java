package com.znahodochka.backend.users.dto;

import lombok.Builder;

@Builder
public record ShortUserInfoDto(Long id, String login, String avatarUri, String name) {
}
