package com.znahodochka.backend.users.services;

import com.znahodochka.backend.users.dto.CreateFollowingDto;
import com.znahodochka.backend.users.dto.FollowingDto;
import com.znahodochka.backend.users.dto.ShortUserInfoDto;

import java.util.List;

public interface FollowingService {
    FollowingDto createFollowing(CreateFollowingDto dto, Long userId);

    void deleteFollowing(Long followingId, Long principal);
    
    boolean isFollowing(Long followerId, Long followingId);

    List<ShortUserInfoDto> getFollowers(Long userId);

    List<ShortUserInfoDto> getFollowings(Long userId);
}
