package com.znahodochka.backend.storage.repositories;

import com.znahodochka.backend.storage.model.AttachmentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AttachmentTypeRepository extends JpaRepository<AttachmentType, Long> {
    Optional<AttachmentType> findByTitle(String title);
}
