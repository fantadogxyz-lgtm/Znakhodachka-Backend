package com.znahodochka.backend.publications.service;

import com.znahodochka.backend.publications.dto.*;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface CommunityService {
    CommunityDto createCommunity(CreateCommunityDto dto, Long creatorId);
    CommunityDto getCommunityBySlug(String slug, Long currentUserId);
    CommunityDto updateCommunity(UUID id, UpdateCommunityDto dto, Long userId);
    void deleteCommunity(UUID id, Long userId);
    List<CommunityTagDto> getTags(UUID communityId);
    CommunityTagDto addTag(UUID communityId, String title, Long userId);
    List<CommunityShortDto> getUserCommunities(Long userId, Long currentUserId);
    List<CommunityShortDto> searchCommunities(String query, Long currentUserId);
    void updateCoverUri(UUID communityId, String coverUri, Long userId);
    void deleteCoverUri(UUID communityId, Long userId);
    boolean isSlugFree(String slug);

    DiscussionDto createDiscussion(CreateDiscussionDto dto, UUID id, Long principal);

    DiscussionDto updateDiscussion(UpdateDiscussionDto dto, UUID communityId, UUID discussionId, Long principal);

    DiscussionDto getDiscussion(UUID communityId, UUID discussionId, Long principal);

    void deleteDiscussion(UUID communityId, UUID discussionId, Long principal);

    FeedResponse getPublicationFeed(Instant cursor, int size, Long principal);

    @Transactional(readOnly = true)
    FeedDiscussionResponse getDiscussionFeed(Instant cursor, int size, Long principal);

    FeedDiscussionResponse getDiscussionTopics(UUID communityId, UUID topicId,
                                               Instant cursor, int size, Long principal);

    FeedDiscussionResponse getDiscussionReplies(UUID communityId, UUID parentId,
                                                Instant cursor, int size, Long principal);
}