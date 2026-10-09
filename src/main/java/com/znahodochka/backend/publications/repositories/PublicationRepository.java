package com.znahodochka.backend.publications.repositories;

import com.znahodochka.backend.publications.model.Publication;
import com.znahodochka.backend.publications.model.PublicationType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface PublicationRepository extends JpaRepository<Publication, UUID> {
    @Query("SELECT p FROM Publication p WHERE p.publicationType <> :excluded ORDER BY p.createdAt DESC")
    List<Publication> findFirstPage(@Param("excluded") PublicationType excluded, Pageable pageable);

    @Query("SELECT p FROM Publication p WHERE p.createdAt < :cursor AND p.publicationType <> :excluded  ORDER BY p.createdAt DESC")
    List<Publication> findNextPage(@Param("cursor") Instant cursor, @Param("excluded") PublicationType excluded, Pageable pageable);

    @Query("""
    SELECT MAX(p.likesCount) 
    FROM Publication p 
    WHERE p.parentPublication.id = :challengeId 
      AND p.publicationType = :type
""")
    Long findMaxLikesForChallenge(@Param("challengeId") UUID challengeId,
                                  @Param("type") PublicationType type);

    @Modifying
    @Query("UPDATE Publication p SET p.likesCount = p.likesCount + 1 WHERE p.id = :publicationId")
    void incrementLikesCount(UUID publicationId);

    @Modifying
    @Query("UPDATE Publication p SET p.likesCount = p.likesCount - 1 WHERE p.id = :publicationId AND p.likesCount > 0")
    void decrementLikesCount(UUID publicationId);

    @Modifying
    @Query("UPDATE Publication p SET p.commentsCount = p.commentsCount + 1 WHERE p.id = :publicationId")
    void incrementCommentsCount(UUID publicationId);

    @Modifying
    @Query("UPDATE Publication p SET p.commentsCount = p.commentsCount WHERE p.id = :publicationId AND p.commentsCount > 0")
    void decrementCommentsCount(UUID publicationId);

    @Query("""
    SELECT p FROM Publication p 
    WHERE p.authorId = :userId 
      AND (:type IS NULL OR p.publicationType = :type)
    ORDER BY p.createdAt DESC
""")
    List<Publication> findUserFirstPage(
            @Param("userId") Long userId,
            @Param("type") PublicationType type,
            Pageable pageable);

    @Query("""
    SELECT p FROM Publication p 
    WHERE p.authorId = :userId 
      AND (:type IS NULL OR p.publicationType = :type)
      AND p.createdAt < :cursor
    ORDER BY p.createdAt DESC
""")
    List<Publication> findUserNextPage(
            @Param("userId") Long userId,
            @Param("type") PublicationType type,
            @Param("cursor") Instant cursor,
            Pageable pageable);

    @Query("""
    SELECT p FROM Publication p 
    WHERE p.parentPublication.id = :challengeId 
    ORDER BY p.likesCount DESC, p.createdAt ASC
""")
    List<Publication> findResponsesByChallengeId(@Param("challengeId") UUID challengeId, Pageable pageable);

    @Query("""
    SELECT p FROM Publication p 
    WHERE p.parentPublication.id = :challengeId 
      AND p.createdAt < :cursor
    ORDER BY p.likesCount DESC, p.createdAt ASC
""")
    List<Publication> findResponsesNextPage(
            @Param("challengeId") UUID challengeId,
            @Param("cursor") Instant cursor,
            Pageable pageable);


    @Query("""
    SELECT p FROM Publication p 
    WHERE p.authorId IN (
        SELECT f.followingId FROM Following f WHERE f.followerId = :userId
    )
    AND p.publicationType <> :excluded
    ORDER BY p.createdAt DESC
""")
    List<Publication> findFollowingFirstPage(
            @Param("userId") Long userId,
            @Param("excluded") PublicationType excluded,
            Pageable pageable);

    @Query("""
    SELECT p FROM Publication p 
    WHERE p.authorId IN (
        SELECT f.followingId FROM Following f WHERE f.followerId = :userId
    )
    AND p.publicationType <> :excluded
    AND p.createdAt < :cursor
    ORDER BY p.createdAt DESC
""")
    List<Publication> findFollowingNextPage(
            @Param("userId") Long userId,
            @Param("excluded") PublicationType excluded,
            @Param("cursor") Instant cursor,
            Pageable pageable);


    @Modifying
    @Query("UPDATE Publication p SET p.commentsCount = p.commentsCount - :count WHERE p.id = :id AND p.commentsCount >= :count")
    void decrementCommentsCountBy(UUID publicationId, long totalToDelete);


    @Query("""
    SELECT p FROM Publication p
    WHERE p.publicationType <> :excluded
      AND (:categoryId IS NULL OR p.category.id = :categoryId)
      AND p.createdAt < :cursor
    ORDER BY p.createdAt DESC
""")
    List<Publication> findFilteredPage(
            @Param("excluded") PublicationType excluded,
            @Param("categoryId") Long categoryId,
            @Param("cursor") Instant cursor,
            Pageable pageable);

    @Query("""
    SELECT DISTINCT p FROM Publication p
    JOIN p.tags t
    WHERE p.publicationType <> :excluded
      AND (:categoryId IS NULL OR p.category.id = :categoryId)
      AND t.id IN :tagIds
      AND p.createdAt < :cursor
    ORDER BY p.createdAt DESC
""")
    List<Publication> findFilteredPageWithTags(
            @Param("excluded") PublicationType excluded,
            @Param("categoryId") Long categoryId,
            @Param("tagIds") Set<Long> tagIds,
            @Param("cursor") Instant cursor,
            Pageable pageable);


    @Query("""
    SELECT DISTINCT p.city FROM Publication p
    WHERE p.city IS NOT NULL AND p.city <> ''
      AND (:region IS NULL OR p.region = :region)
    ORDER BY p.city
""")
    List<String> findDistinctCities(@Param("region") String region);

    @Query("""
    SELECT p FROM Publication p
    WHERE p.publicationType <> :excluded
      AND (:categoryId IS NULL OR p.category.id = :categoryId)
      AND (:region IS NULL OR p.region = :region)
      AND (:city IS NULL OR p.city = :city)
      AND p.createdAt < :cursor
    ORDER BY p.createdAt DESC
""")
    List<Publication> findFilteredPageWithLocation(
            @Param("excluded") PublicationType excluded,
            @Param("categoryId") Long categoryId,
            @Param("region") String region,
            @Param("city") String city,
            @Param("cursor") Instant cursor,
            Pageable pageable);


    @Query(value = """
    SELECT * FROM publications.publications p
    WHERE p.publication_type <> 'CHALLENGE_RESPONSE'
      AND p.location IS NOT NULL
      AND ST_DWithin(
            p.location,
            ST_MakePoint(:lon, :lat)::geography,
            :radiusMeters
          )
    ORDER BY random()
    LIMIT 10
""", nativeQuery = true)
    List<Publication> findRandomInRadius(
            @Param("lat") double lat,
            @Param("lon") double lon,
            @Param("radiusMeters") double radiusMeters);


    @Query(value = """
    SELECT * FROM publications.publications p
    WHERE p.publication_type <> 'CHALLENGE_RESPONSE'
      AND p.category_id = :categoryId
      AND p.location IS NOT NULL
      AND ST_DWithin(
            p.location,
            ST_MakePoint(:lon, :lat)::geography,
            :radiusMeters
          )
    ORDER BY random()
    LIMIT 10
""", nativeQuery = true)
    List<Publication> findRandomInRadiusWithCategory(
            @Param("lat") double lat,
            @Param("lon") double lon,
            @Param("radiusMeters") double radiusMeters,
            @Param("categoryId") Long categoryId);

    @Query(value = """
    SELECT DISTINCT p.* FROM publications.publications p
    JOIN publications.publication_tags pt ON pt.publication_id = p.id
    WHERE p.publication_type <> 'CHALLENGE_RESPONSE'
      AND (:categoryId IS NULL OR p.category_id = :categoryId)
      AND pt.tag_id IN (:tagIds)
      AND p.location IS NOT NULL
      AND ST_DWithin(
            p.location,
            ST_MakePoint(:lon, :lat)::geography,
            :radiusMeters
          )
    ORDER BY random()
    LIMIT 10
""", nativeQuery = true)
    List<Publication> findRandomInRadiusWithCategoryAndTags(
            @Param("lat") double lat,
            @Param("lon") double lon,
            @Param("radiusMeters") double radiusMeters,
            @Param("categoryId") Long categoryId,
            @Param("tagIds") Set<Long> tagIds);
}
