package com.znahodochka.backend.publications.repositories;

import com.znahodochka.backend.publications.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LikeRepository extends JpaRepository<Like, UUID> {
    boolean existsByPublicationIdAndAuthorId(UUID publicationId, Long authorId);
    Optional<Like> findByPublicationIdAndAuthorId(UUID publicationId, Long authorId);
    List<Like> findAllByPublicationId(UUID publicationId);
    long countByPublicationId(UUID publicationId);
}
