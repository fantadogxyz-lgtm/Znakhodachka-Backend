package com.znahodochka.backend.storage.service;

import com.znahodochka.backend.exceptions.BadRequestException;
import com.znahodochka.backend.exceptions.BusinessExceptionCode;
import com.znahodochka.backend.exceptions.ResourceNotFoundException;
import com.znahodochka.backend.publications.model.Discussion;
import com.znahodochka.backend.publications.model.Publication;
import com.znahodochka.backend.publications.repositories.DiscussionRepository;
import com.znahodochka.backend.publications.repositories.PublicationRepository;
import com.znahodochka.backend.storage.dto.AttachmentDto;
import com.znahodochka.backend.storage.dto.DiscussionAttachmentDto;
import com.znahodochka.backend.storage.dto.UpdateAttachmentDto;
import com.znahodochka.backend.storage.dto.UpdateDiscussionAttachmentDto;
import com.znahodochka.backend.storage.model.Attachment;
import com.znahodochka.backend.storage.model.Bucket;
import com.znahodochka.backend.storage.model.DiscussionAttachment;
import com.znahodochka.backend.storage.repositories.AttachmentRepository;
import com.znahodochka.backend.storage.repositories.AttachmentTypeRepository;
import com.znahodochka.backend.storage.repositories.BucketRepository;
import com.znahodochka.backend.storage.repositories.DiscussionAttachmentRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {
    private final AttachmentRepository attachmentRepository;
    private final DiscussionAttachmentRepository discussionAttachmentRepository; // ДОБАВИЛИ
    private final MinioService minioService;
    private final BucketRepository bucketRepository;
    private final AttachmentTypeRepository attachmentTypeRepository;
    private final PublicationRepository publicationRepository;
    private final DiscussionRepository discussionRepository; // ДОБАВИЛИ


    @Override
    @Transactional
    public AttachmentDto upload(MultipartFile file, Long userId) {
        Bucket bucket = checkOrCreateUserBucket(userId);

        Attachment attachment = Attachment.builder()
                .type(detectTypeId(file.getOriginalFilename()))
                .bucket(bucket)
                .publicationId(null)
                .photoOrder(null)
                .build();

        Attachment saved = attachmentRepository.save(attachment);
        UUID attachmentId = saved.getId();

        String extension = extractExtension(file.getOriginalFilename());
        String fileName = attachmentId + "." + extension;

        minioService.uploadFile(bucket.getBucketName(), fileName, file);

        saved.setFileUri(attachmentId + "." + extension);
        attachmentRepository.save(saved);

        return toDto(saved);
    }

    @Override
    @Transactional
    public DiscussionAttachmentDto uploadForDiscussion(MultipartFile file, Long userId) {
        Bucket bucket = checkOrCreateUserBucket(userId);

        DiscussionAttachment attachment = DiscussionAttachment.builder()
                .type(detectTypeId(file.getOriginalFilename()))
                .bucket(bucket)
                .discussionId(null)
                .photoOrder(null)
                .build();

        DiscussionAttachment saved = discussionAttachmentRepository.save(attachment);
        UUID attachmentId = saved.getId();

        String extension = extractExtension(file.getOriginalFilename());
        String fileName = attachmentId + "." + extension;

        minioService.uploadFile(bucket.getBucketName(), fileName, file);

        saved.setFileUri(attachmentId + "." + extension);
        discussionAttachmentRepository.save(saved);

        return toDiscussionDto(saved);
    }

    @Override
    public List<AttachmentDto> getByPublicationId(UUID publicationId) {
        return attachmentRepository.findByPublicationIdOrderByPhotoOrder(publicationId).stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public List<DiscussionAttachmentDto> getByDiscussionId(UUID discussionId) {
        return discussionAttachmentRepository.findAllByDiscussionId(discussionId).stream()
                .map(this::toDiscussionDto)
                .toList();
    }


    @Override
    public void attachToPublication(List<UUID> attachmentIds, UUID publicationId) {
        List<Attachment> attachments = attachmentRepository.findAllById(attachmentIds);
        for (int i = 0; i < attachments.size(); i++) {
            attachments.get(i).setPublicationId(publicationId);
            attachments.get(i).setPhotoOrder((long) i);
        }
        attachmentRepository.saveAll(attachments);
    }

    @Override
    @Transactional
    public void attachToDiscussion(List<UUID> attachmentIds, UUID discussionId) {
        if (attachmentIds == null || attachmentIds.isEmpty()) return;
        List<DiscussionAttachment> attachments = discussionAttachmentRepository.findAllById(attachmentIds);
        for (int i = 0; i < attachments.size(); i++) {
            attachments.get(i).setDiscussionId(discussionId);
            attachments.get(i).setPhotoOrder((long) i);
        }
        discussionAttachmentRepository.saveAll(attachments);
    }

    @Override
    public Resource downloadFile(String fileUri) {
        var attachment = attachmentRepository.findByFileUri(fileUri);
        String bucketName;

        if (attachment.isPresent()) {
            bucketName = attachment.get().getBucket().getBucketName();
        } else {
            var discussionAttachment = discussionAttachmentRepository.findByFileUri(fileUri)
                    .orElseThrow(ResourceNotFoundException::new);
            bucketName = discussionAttachment.getBucket().getBucketName();
        }

        try {
            InputStream stream = minioService.downloadFile(bucketName, fileUri);
            return new InputStreamResource(stream);
        } catch (Exception e) {
            throw new RuntimeException("Failed to download file", e);
        }
    }

    @Override
    @Transactional
    public void updatePublicationAttachments(UUID publicationId, List<UpdateAttachmentDto> attachments, Long userId) {
        if (attachments == null) return;

        Publication publication = publicationRepository.findById(publicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Publication not found"));
        if (!publication.getAuthorId().equals(userId)) {
            throw new BadRequestException(BusinessExceptionCode.NOT_YOUR_PUBLICATION);
        }

        Set<UUID> keepIds = new HashSet<>();
        for (UpdateAttachmentDto dto : attachments) {
            if (dto.id() != null) keepIds.add(dto.id());
            if (dto.attachmentId() != null) keepIds.add(dto.attachmentId());
        }

        List<Attachment> current = attachmentRepository.findByPublicationIdOrderByPhotoOrder(publicationId);
        for (Attachment att : current) {
            if (!keepIds.contains(att.getId())) {
                minioService.deleteFile(att.getBucket().getBucketName(), att.getFileUri());
                attachmentRepository.delete(att);
            }
        }

        for (UpdateAttachmentDto dto : attachments) {
            UUID attId = dto.id() != null ? dto.id() : dto.attachmentId();
            if (attId == null) continue;

            Attachment att = attachmentRepository.findById(attId)
                    .orElseThrow(() -> new ResourceNotFoundException("Attachment not found: " + attId));

            att.setPublicationId(publicationId);
            att.setPhotoOrder(dto.order());
            attachmentRepository.save(att);
        }
    }

    @Override
    @Transactional
    public void updateDiscussionAttachments(UUID discussionId, List<UpdateDiscussionAttachmentDto> attachments, Long userId) {
        if (attachments == null) return;

        Discussion discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new ResourceNotFoundException("Discussion not found"));
        if (!discussion.getAuthorId().equals(userId)) {
            throw new BadRequestException(BusinessExceptionCode.NOT_YOUR_DISCUSSION);
        }

        Set<UUID> keepIds = new HashSet<>();
        for (UpdateDiscussionAttachmentDto dto : attachments) {
            if (dto.id() != null) keepIds.add(dto.id());
            if (dto.attachmentId() != null) keepIds.add(dto.attachmentId());
        }

        List<DiscussionAttachment> current = discussionAttachmentRepository.findAllByDiscussionId(discussionId);
        for (DiscussionAttachment att : current) {
            if (!keepIds.contains(att.getId())) {
                minioService.deleteFile(att.getBucket().getBucketName(), att.getFileUri());
                discussionAttachmentRepository.delete(att);
            }
        }

        for (UpdateDiscussionAttachmentDto dto : attachments) {
            UUID attId = dto.id() != null ? dto.id() : dto.attachmentId();
            if (attId == null) continue;

            DiscussionAttachment att = discussionAttachmentRepository.findById(attId)
                    .orElseThrow(() -> new ResourceNotFoundException("Discussion attachment not found: " + attId));

            att.setDiscussionId(discussionId);
            att.setPhotoOrder(dto.order());
            discussionAttachmentRepository.save(att);
        }
    }

    @Transactional
    protected Bucket checkOrCreateUserBucket(Long userId) {
        if (bucketRepository.existsByOwnerId(userId)) {
            return bucketRepository.findByOwnerId(userId).orElseThrow();
        }

        Bucket newBucket = Bucket.builder()
                .ownerId(userId)
                .bucketName("user-" + userId)
                .build();
        newBucket = bucketRepository.save(newBucket);

        try {
            minioService.checkOrCreateBucket(newBucket.getBucketName());
        } catch (Exception e) {
            throw new RuntimeException("Error while creating bucket in MinIO", e);
        }

        return newBucket;
    }

    private String extractExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            throw new BadRequestException(BusinessExceptionCode.INVALID_FILE);
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }

    private Long detectTypeId(String filename) {
        String ext = extractExtension(filename);
        return attachmentTypeRepository.findByTitle(ext)
                .orElseThrow(() -> new BadRequestException(BusinessExceptionCode.UNSUPPORTED_FILE_TYPE))
                .getId();
    }

    private AttachmentDto toDto(Attachment a) {
        return AttachmentDto.builder()
                .id(a.getId())
                .fileUri(a.getFileUri())
                .photoOrder(a.getPhotoOrder())
                .type(a.getType())
                .build();
    }

    private DiscussionAttachmentDto toDiscussionDto(DiscussionAttachment a) {
        return DiscussionAttachmentDto.builder()
                .id(a.getId())
                .fileUri(a.getFileUri())
                .photoOrder(a.getPhotoOrder())
                .type(a.getType())
                .build();
    }
}