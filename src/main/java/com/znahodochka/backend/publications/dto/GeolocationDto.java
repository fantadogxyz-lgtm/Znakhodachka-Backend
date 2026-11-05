package com.znahodochka.backend.publications.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record GeolocationDto(
        UUID id,
        String title,
        Double latitude,
        Double longitude
) {}