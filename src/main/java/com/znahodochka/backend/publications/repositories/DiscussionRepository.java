package com.znahodochka.backend.publications.repositories;

import com.znahodochka.backend.publications.model.Discussion;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Collection;
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

    @Query("SELECT COUNT(d) FROM Discussion d WHERE d.topic.id = :topicId")
    long countByTopicId(@Param("topicId") UUID topicId);

    @Query("SELECT COUNT(d) FROM Discussion d WHERE d.parent.id = :parentId")
    long countByParentId(@Param("parentId") UUID parentId);

    @Query("""
        SELECT d.topic.id, COUNT(d) FROM Discussion d
        WHERE d.topic.id IN :topicIds
        GROUP BY d.topic.id
    """)
    List<Object[]> countGroupedByTopicIds(@Param("topicIds") Collection<UUID> topicIds);

    @Query("""
        SELECT d.parent.id, COUNT(d) FROM Discussion d
        WHERE d.parent.id IN :parentIds
        GROUP BY d.parent.id
    """)
    List<Object[]> countGroupedByParentIds(@Param("parentIds") Collection<UUID> parentIds);

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

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "DELETE FROM publications.discussions WHERE community_id = :communityId", nativeQuery = true)
    void deleteAllByCommunityIdNative(@Param("communityId") UUID communityId);
}