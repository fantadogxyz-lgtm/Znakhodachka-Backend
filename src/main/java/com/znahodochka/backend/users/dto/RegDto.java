package com.znahodochka.backend.users.dto;

import lombok.Builder;

@Builder
public record RegDto(String login, String email, String password, String name, String bio, String avatar_uri) {
}
