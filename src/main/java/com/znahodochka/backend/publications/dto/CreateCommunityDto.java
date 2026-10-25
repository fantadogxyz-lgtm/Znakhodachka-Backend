package com.znahodochka.backend.publications.dto;

import java.util.List;

public record CreateCommunityDto(
        String slug,
        String title,
        String description,
        String coverUri,
        List<String> tags
) {}