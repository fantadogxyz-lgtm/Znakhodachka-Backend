package com.znahodochka.backend.storage.repositories;

import com.znahodochka.backend.storage.model.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {
    List<Attachment> findByPublicationIdOrderByPhotoOrder(UUID publicationId);

    List<Attachment> findAllByIdIn(List<UUID> ids);

    Optional<Attachment> findByFileUri(String fileUri);

}
