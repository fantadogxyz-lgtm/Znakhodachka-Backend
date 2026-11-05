package com.znahodochka.backend.publications.repositories;

import com.znahodochka.backend.publications.model.CommunityParticipation;
import com.znahodochka.backend.users.dto.ShortUserInfoDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CommunityParticipationRepository extends JpaRepository<CommunityParticipation, Long> {


    boolean existsByCommunityIdAndUserId(UUID communityId, Long userId);

    CommunityParticipation getFirstByCommunityIdAndUserId(UUID communityId, Long userId);

    @Query("""
            select new com.znahodochka.backend.users.dto.ShortUserInfoDto(
                u.id, u.login, u.avatarUri, u.name
            )
            from CommunityParticipation cp
            join User u on u.id = cp.userId
            where cp.communityId = :communityId
            order by cp.joinedAt desc
            """)
    List<ShortUserInfoDto> findMembersByCommunityId(@Param("communityId") UUID communityId, Pageable pageable);
}
