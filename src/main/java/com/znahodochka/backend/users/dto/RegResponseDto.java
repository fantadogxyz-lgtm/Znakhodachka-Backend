package com.znahodochka.backend.users.dto;

import lombok.Builder;

@Builder
public record RegResponseDto(String token) {
}
