package com.znahodochka.backend.storage.service;

import com.znahodochka.backend.storage.dto.AttachmentDto;
import com.znahodochka.backend.storage.dto.UpdateAttachmentDto;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface AttachmentService {
    AttachmentDto upload(MultipartFile file, Long user_id) throws IOException;
    List<AttachmentDto> getByPublicationId(UUID publicationId);
    void attachToPublication(List<UUID> attachmentIds, UUID publicationId);
    Resource downloadFile(String fileUri);
    void updatePublicationAttachments(UUID publicationId, List<UpdateAttachmentDto> attachments, Long userId);
}
