package com.znahodochka.backend.publications.service;

import java.util.UUID;

public interface DiscussionLikeService {
    void deleteDiscussionLike(UUID communityId, UUID discussionId, Long principal);

    void createDiscussionLike(UUID communityId, UUID discussionId, Long principal);
}
