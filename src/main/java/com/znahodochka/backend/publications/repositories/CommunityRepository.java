package com.znahodochka.backend.publications.repositories;

import com.znahodochka.backend.publications.model.Community;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommunityRepository extends JpaRepository<Community, UUID> {


    Optional<Community> findBySlug(String slug);

    long countByCreatorId(Long creatorId);

    List<Community> findByCreatorId(Long creatorId);

    @Query(value = """
        SELECT c.* FROM publications.communities c
        JOIN publications.community_members cm ON cm.community_id = c.id
        WHERE cm.user_id = :userId
        ORDER BY CASE WHEN c.creator_id = :userId THEN 0 ELSE 1 END,
                 c.created_at DESC
    """, nativeQuery = true)
    List<Community> findUserCommunities(@Param("userId") Long userId);

    @Query(value = """
    SELECT * FROM publications.communities c
    WHERE LOWER(c.slug) LIKE LOWER(:query) || '%'
       OR LOWER(c.slug) LIKE '%' || LOWER(:query) || '%'
       OR LOWER(c.title) LIKE '%' || LOWER(:query) || '%'
    ORDER BY
        CASE
            WHEN LOWER(c.slug) = LOWER(:query) THEN 0
            WHEN LOWER(c.slug) LIKE LOWER(:query) || '%' THEN 1
            WHEN LOWER(c.title) LIKE LOWER(:query) || '%' THEN 2
            ELSE 3
        END,
        c.slug
    LIMIT 30
""", nativeQuery = true)
    List<Community> searchBySlugOrTitle(@Param("query") String query);

    boolean existsBySlug(String slug);
}
