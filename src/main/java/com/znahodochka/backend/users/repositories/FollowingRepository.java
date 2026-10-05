package com.znahodochka.backend.users.repositories;

import com.znahodochka.backend.users.model.Following;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FollowingRepository extends JpaRepository<Following, UUID> {
    List<Following> getAllByFollowingId(Long loginId);

    List<Following> getAllByFollowerId(Long loginId);

    boolean existsByFollowerIdAndFollowingId(Long follower, Long following);

    Optional<Following> findByFollowerIdAndFollowingId(Long principal, Long followingId);
}
