package com.znahodochka.backend.publications.repositories;

import com.znahodochka.backend.publications.model.Discussion;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface DiscussionRepository extends JpaRepository<Discussion, UUID> {

    @Modifying
    @Query("UPDATE Discussion p SET p.countOfLikes = p.countOfLikes + 1 WHERE p.id = :discussionId")
    void incrementLikesCount(UUID discussionId);

    @Modifying
    @Query("UPDATE Discussion p SET p.countOfLikes = p.countOfLikes - 1 WHERE p.id = :discussionId AND p.countOfLikes > 0")
    void decrementLikesCount(UUID discussionId);

    List<Discussion> findAllByParentId(UUID parentId);

    // ==== ЛЕНТА ТОПИКОВ (только MAIN, от новых к старым) ====
    @Query("""
        SELECT d FROM Discussion d
        WHERE d.community IS NOT NULL
          AND d.type = com.znahodochka.backend.publications.model.DiscussionType.MAIN
        ORDER BY d.createdAt DESC
    """)
    List<Discussion> findCommunityFeedFirstPage(Pageable pageable);

    @Query("""
        SELECT d FROM Discussion d
        WHERE d.community IS NOT NULL
          AND d.type = com.znahodochka.backend.publications.model.DiscussionType.MAIN
          AND d.createdAt < :cursor
        ORDER BY d.createdAt DESC
    """)
    List<Discussion> findCommunityFeedNextPage(@Param("cursor") Instant cursor, Pageable pageable);

    // ==== КОММЕНТАРИИ К ТОПИКУ (от новых к старым) ====
    @Query("""
        SELECT d FROM Discussion d
        WHERE d.topic.id = :topicId
          AND d.community.id = :communityId
        ORDER BY d.createdAt DESC
    """)
    List<Discussion> findTopicFirstPage(@Param("communityId") UUID communityId,
                                        @Param("topicId") UUID topicId,
                                        Pageable pageable);

    @Query("""
        SELECT d FROM Discussion d
        WHERE d.topic.id = :topicId
          AND d.community.id = :communityId
          AND d.createdAt < :cursor
        ORDER BY d.createdAt DESC
    """)
    List<Discussion> findTopicNextPage(@Param("communityId") UUID communityId,
                                       @Param("topicId") UUID topicId,
                                       @Param("cursor") Instant cursor,
                                       Pageable pageable);

    // ==== ОТВЕТЫ НА КОММЕНТ (от новых к старым) ====
    @Query("""
        SELECT d FROM Discussion d
        WHERE d.parent.id = :parentId
          AND d.community.id = :communityId
        ORDER BY d.createdAt DESC
    """)
    List<Discussion> findRepliesFirstPage(@Param("communityId") UUID communityId,
                                          @Param("parentId") UUID parentId,
                                          Pageable pageable);

    @Query("""
        SELECT d FROM Discussion d
        WHERE d.parent.id = :parentId
          AND d.community.id = :communityId
          AND d.createdAt < :cursor
        ORDER BY d.createdAt DESC
    """)
    List<Discussion> findRepliesNextPage(@Param("communityId") UUID communityId,
                                         @Param("parentId") UUID parentId,
                                         @Param("cursor") Instant cursor,
                                         Pageable pageable);
}
