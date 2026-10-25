package com.znahodochka.backend.publications.dto;

public record UpdateCommunityDto(
        String slug,
        String title,
        String description
) {}