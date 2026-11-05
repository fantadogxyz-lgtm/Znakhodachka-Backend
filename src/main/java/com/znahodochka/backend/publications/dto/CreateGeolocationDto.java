package com.znahodochka.backend.publications.dto;

import lombok.Builder;

@Builder
public record CreateGeolocationDto(
        String title,
        Double latitude,
        Double longitude
) {
}
