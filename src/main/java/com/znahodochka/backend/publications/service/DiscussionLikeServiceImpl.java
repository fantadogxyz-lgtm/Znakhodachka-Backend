package com.znahodochka.backend.publications.service;

import com.znahodochka.backend.exceptions.BadRequestException;
import com.znahodochka.backend.exceptions.BusinessExceptionCode;
import com.znahodochka.backend.exceptions.ResourceNotFoundException;
import com.znahodochka.backend.publications.model.DiscussionLike;
import com.znahodochka.backend.publications.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DiscussionLikeServiceImpl implements DiscussionLikeService {
    private final CommunityRepository communityRepository;
    private final CommunityParticipationRepository  communityParticipationRepository;
    private final DiscussionLikeRepository discussionLikeRepository;
    private final DiscussionRepository discussionRepository;

    @Override
    @Transactional
    public void deleteDiscussionLike(UUID communityId, UUID discussionId, Long principal) {
        if(!communityRepository.existsById(communityId)){
            throw new ResourceNotFoundException("Community not found");
        }

        if(communityParticipationRepository.existsByCommunityIdAndUserId(communityId,principal)){
            throw new BadRequestException(BusinessExceptionCode.NOT_YOUR_COMMUNITY);
        } if(!communityRepository.existsById(communityId)){
            throw new ResourceNotFoundException("Community not found");
        }

        if(communityParticipationRepository.existsByCommunityIdAndUserId(communityId,principal)){
            throw new BadRequestException(BusinessExceptionCode.NOT_YOUR_COMMUNITY);
        }

        if(!discussionLikeRepository.existsByDiscussionIdAndAuthorId(discussionId,principal)){
            throw new BadRequestException(BusinessExceptionCode.ALREADY_DISLIKED);
        }
        DiscussionLike discussionLike = discussionLikeRepository.findByDiscussionIdAndAuthorId(discussionId, principal)
                .orElseThrow(()->new ResourceNotFoundException("Discussion like not found"));
        discussionRepository.decrementLikesCount(discussionId);
        discussionLikeRepository.delete(discussionLike);


    }

    @Override
    @Transactional
    public void createDiscussionLike(UUID communityId, UUID discussionId, Long principal) {
        if(!communityRepository.existsById(communityId)){
            throw new ResourceNotFoundException("Community not found");
        }

        if(communityParticipationRepository.existsByCommunityIdAndUserId(communityId,principal)){
            throw new BadRequestException(BusinessExceptionCode.NOT_YOUR_COMMUNITY);
        } if(!communityRepository.existsById(communityId)){
            throw new ResourceNotFoundException("Community not found");
        }

        if(communityParticipationRepository.existsByCommunityIdAndUserId(communityId,principal)){
            throw new BadRequestException(BusinessExceptionCode.NOT_YOUR_COMMUNITY);
        }

        if(discussionLikeRepository.existsByDiscussionIdAndAuthorId(discussionId,principal)){
            throw new BadRequestException(BusinessExceptionCode.ALREADY_LIKED);
        }
        DiscussionLike discussionLike = new DiscussionLike();
        discussionLike.setDiscussion(
                discussionRepository.findById(discussionId).orElseThrow(
                        () -> new ResourceNotFoundException("Discussion not found")
                )
        );
        discussionLike.setAuthorId(principal);
        discussionLikeRepository.save(discussionLike);
        discussionRepository.incrementLikesCount(discussionId);

    }
}
