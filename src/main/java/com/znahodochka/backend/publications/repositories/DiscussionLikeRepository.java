package com.znahodochka.backend.publications.repositories;

import com.znahodochka.backend.publications.model.DiscussionLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DiscussionLikeRepository extends JpaRepository<DiscussionLike, Long> {
    Boolean existsByDiscussionIdAndAuthorId(UUID id, Long principal);


    DiscussionLike findById(UUID discussionId);

    Optional<DiscussionLike> findByDiscussionIdAndAuthorId(UUID discussionId, Long principal);
}
