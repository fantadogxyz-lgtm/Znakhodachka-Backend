package com.znahodochka.backend.publications.dto;

import lombok.Builder;

@Builder
public record CommunityTagDto(Long id, String title) {
}
