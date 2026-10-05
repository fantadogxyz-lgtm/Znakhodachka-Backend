package com.znahodochka.backend.storage.dto;

import lombok.Builder;

import java.util.UUID;

@Builder(toBuilder = true)
public record AttachmentDto(    UUID id,
                                String fileUri,
                                Long photoOrder,
                                Long type) {

}
