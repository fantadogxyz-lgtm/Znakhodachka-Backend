package com.znahodochka.backend.publications.service;

import com.znahodochka.backend.exceptions.BadRequestException;
import com.znahodochka.backend.exceptions.BusinessExceptionCode;
import com.znahodochka.backend.exceptions.ResourceNotFoundException;
import com.znahodochka.backend.publications.model.Community;
import com.znahodochka.backend.publications.model.CommunityParticipation;
import com.znahodochka.backend.publications.repositories.CommunityParticipationRepository;
import com.znahodochka.backend.publications.repositories.CommunityRepository;
import com.znahodochka.backend.users.dto.ShortUserInfoDto;
import com.znahodochka.backend.users.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CommunityMemberServiceImpl implements CommunityMemberService {

    private final CommunityRepository communityRepository;
    private final UserService userService;
    private final CommunityParticipationRepository communityParticipationRepository;

    @Override
    @Transactional
    public void joinCommunity(UUID communityId, Long userId) {

        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new ResourceNotFoundException("Community not found"));

        if (communityParticipationRepository.existsById_CommunityIdAndId_UserId(communityId, userId)) {
            throw new BadRequestException(BusinessExceptionCode.ALREADY_MEMBER);
        }

        community.setMembersCount(community.getMembersCount() + 1);
        communityRepository.save(community);

        communityParticipationRepository.save(CommunityParticipation.builder()
                .communityId(communityId)
                .userId(userId)
                .build());


    }

    @Override
    @Transactional
    public void leaveCommunity(UUID communityId, Long userId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new ResourceNotFoundException("Community not found"));

        if (community.getCreatorId().equals(userId)) {
            throw new BadRequestException(BusinessExceptionCode.CREATOR_CANNOT_LEAVE);
        }
        if (!communityParticipationRepository.existsById_CommunityIdAndId_UserId(communityId, userId)) {
            throw new BadRequestException(BusinessExceptionCode.NOT_A_MEMBER);
        }

        community.setMembersCount(community.getMembersCount() - 1);
        communityRepository.save(community);

        CommunityParticipation communityParticipation = communityParticipationRepository.getFirstByCommunityIdAndUserId(communityId, userId);
        communityParticipationRepository.delete(communityParticipation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShortUserInfoDto> getMembers(UUID communityId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return communityParticipationRepository.findMembersByCommunityId(communityId, pageable);
    }
}