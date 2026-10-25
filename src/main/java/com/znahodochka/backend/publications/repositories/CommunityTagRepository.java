package com.znahodochka.backend.publications.repositories;

import com.znahodochka.backend.publications.model.CommunityTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommunityTagRepository extends JpaRepository<CommunityTag, Long> {

    List<CommunityTag> findByCommunityId(UUID communityId);

    long countByCommunityId(UUID communityId);

    boolean existsByCommunityIdAndTitle(UUID communityId, String title);
}
