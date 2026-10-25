package com.znahodochka.backend.storage.repositories;

import com.znahodochka.backend.storage.dto.DiscussionAttachmentDto;
import com.znahodochka.backend.storage.model.DiscussionAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DiscussionAttachmentRepository extends JpaRepository<DiscussionAttachment, Long> {
    List<DiscussionAttachment> findAllByDiscussionId(UUID id);
}
