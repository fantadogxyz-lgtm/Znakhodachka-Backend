package com.znahodochka.backend.publications.repositories;

import com.znahodochka.backend.publications.model.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {

    List<Comment> findAllByPublicationIdAndParentIsNullOrderByCreatedAtDesc(UUID publicationId);

    List<Comment> findAllByParentIdOrderByCreatedAtAsc(UUID parentId);

    List<Comment> findAllByParentIdOrderByCreatedAtAsc(UUID parentId, Pageable pageable);

    long countByPublicationId(UUID publicationId);

    long countByParentId(UUID parentId);
}
