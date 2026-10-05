package com.znahodochka.backend.users.dto;

import lombok.Builder;

@Builder
public record UpdateUserInfoDto(String name, String bio) {
}
