package com.znahodochka.backend.publications.service;

import com.znahodochka.backend.users.dto.ShortUserInfoDto;

import java.util.List;
import java.util.UUID;

public interface CommunityMemberService {
    void joinCommunity(UUID communityId, Long userId);
    void leaveCommunity(UUID communityId, Long userId);
    List<ShortUserInfoDto> getMembers(UUID communityId, int page, int size);
}