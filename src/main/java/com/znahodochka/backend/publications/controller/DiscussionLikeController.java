package com.znahodochka.backend.publications.controller;

import com.znahodochka.backend.publications.dto.FeedResponse;
import com.znahodochka.backend.publications.service.DiscussionLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/{communityId}/discussions/{discussionId}/like")
@RequiredArgsConstructor
public class DiscussionLikeController {

    private final DiscussionLikeService discussionLikeService;

    @PostMapping()
    public void createDiscussionLike(
            UsernamePasswordAuthenticationToken token,
            @PathVariable UUID communityId,
            @PathVariable UUID discussionId
    ){
        discussionLikeService.createDiscussionLike(communityId, discussionId, (Long) token.getPrincipal());
    }


    @DeleteMapping()
    public void deleteDiscussionLike(
            UsernamePasswordAuthenticationToken token,
            @PathVariable UUID communityId,
            @PathVariable UUID discussionId
    ){
        discussionLikeService.deleteDiscussionLike(communityId, discussionId, (Long) token.getPrincipal());
    }

}
