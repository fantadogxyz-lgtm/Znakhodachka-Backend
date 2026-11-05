package com.znahodochka.backend.storage.service;

import com.znahodochka.backend.storage.dto.AttachmentDto;
import com.znahodochka.backend.storage.dto.DiscussionAttachmentDto;
import com.znahodochka.backend.storage.dto.UpdateAttachmentDto;
import com.znahodochka.backend.storage.dto.UpdateDiscussionAttachmentDto;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface AttachmentService {
    AttachmentDto upload(MultipartFile file, Long userId) throws IOException;

    DiscussionAttachmentDto uploadForDiscussion(MultipartFile file, Long userId) throws IOException;
    List<AttachmentDto> getByPublicationId(UUID publicationId);
    List<DiscussionAttachmentDto> getByDiscussionId(UUID discussionId);

    void attachToPublication(List<UUID> attachmentIds, UUID publicationId);
    void attachToDiscussion(List<UUID> attachmentIds, UUID discussionId);

    void updatePublicationAttachments(UUID publicationId, List<UpdateAttachmentDto> attachments, Long userId);
    void updateDiscussionAttachments(UUID discussionId, List<UpdateDiscussionAttachmentDto> attachments, Long userId);

    Resource downloadFile(String fileUri);
}