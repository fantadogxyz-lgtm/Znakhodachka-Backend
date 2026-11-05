package com.znahodochka.backend.publications.controller;

import com.znahodochka.backend.publications.dto.*;
import com.znahodochka.backend.publications.service.CommunityMemberService;
import com.znahodochka.backend.publications.service.CommunityService;
import com.znahodochka.backend.users.dto.ShortUserInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/community")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('BASIC')")
public class CommunityController {

    private final CommunityService communityService;
    private final CommunityMemberService communityMemberService;

    @PostMapping
    public CommunityDto create(@RequestBody CreateCommunityDto dto, UsernamePasswordAuthenticationToken token) {
        return communityService.createCommunity(dto, (Long) token.getPrincipal());
    }

    @GetMapping("/{slug}")
    public CommunityDto getBySlug(@PathVariable String slug, UsernamePasswordAuthenticationToken token) {
        return communityService.getCommunityBySlug(slug, (Long) token.getPrincipal());
    }

    @GetMapping("/free-slug")
    public boolean isSlugFree(@RequestParam String slug) {
        return communityService.isSlugFree(slug);
    }

    @PutMapping("/{id}")
    public CommunityDto update(@PathVariable UUID id, @RequestBody UpdateCommunityDto dto, UsernamePasswordAuthenticationToken token) {
        return communityService.updateCommunity(id, dto, (Long) token.getPrincipal());
    }

    @GetMapping("/{id}/tags")
    public List<CommunityTagDto> getTags(@PathVariable UUID id) {
        return communityService.getTags(id);
    }

    @PostMapping("/{id}/tags")
    public CommunityTagDto addTag(@PathVariable UUID id, @RequestParam String title, UsernamePasswordAuthenticationToken token) {
        return communityService.addTag(id, title, (Long) token.getPrincipal());
    }

    @PostMapping("/{id}/join")
    public void join(@PathVariable UUID id, UsernamePasswordAuthenticationToken token) {
        communityMemberService.joinCommunity(id, (Long) token.getPrincipal());
    }

    @DeleteMapping("/{id}/leave")
    public void leave(@PathVariable UUID id, UsernamePasswordAuthenticationToken token) {
        communityMemberService.leaveCommunity(id, (Long) token.getPrincipal());
    }

    @GetMapping("/{id}/members")
    public List<ShortUserInfoDto> getMembers(@PathVariable UUID id, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return communityMemberService.getMembers(id, page, size);
    }

    @GetMapping("/user/{userId}")
    public List<CommunityShortDto> getUserCommunities(@PathVariable Long userId, UsernamePasswordAuthenticationToken token) {
        return communityService.getUserCommunities(userId, (Long) token.getPrincipal());
    }

    @GetMapping("/search")
    public List<CommunityShortDto> searchCommunities(@RequestParam String query, UsernamePasswordAuthenticationToken token) {
        return communityService.searchCommunities(query, (Long) token.getPrincipal());
    }

    @PutMapping("/{id}/cover_uri")
    public void updateCoverUri(@PathVariable UUID id, @RequestParam String coverUri, UsernamePasswordAuthenticationToken token) {
        communityService.updateCoverUri(id, coverUri, (Long) token.getPrincipal());
    }

    @DeleteMapping("/{id}/cover_uri")
    public void deleteCoverUri(@PathVariable UUID id, UsernamePasswordAuthenticationToken token) {
        communityService.deleteCoverUri(id, (Long) token.getPrincipal());
    }




    @PostMapping("/{id}/discussions")
    public DiscussionDto createDiscussion(@RequestBody CreateDiscussionDto dto,
                                          UsernamePasswordAuthenticationToken token,
                                          @PathVariable UUID id) {
        return communityService.createDiscussion(dto, id, (Long) token.getPrincipal());
    }

    @PutMapping("/{communityId}/discussions/{discussionId}")
    public DiscussionDto updateDiscussion(UsernamePasswordAuthenticationToken token,
                                          @PathVariable UUID communityId,
                                          @PathVariable UUID discussionId,
                                          @RequestBody UpdateDiscussionDto dto) {
        return communityService.updateDiscussion(dto, communityId, discussionId, (Long) token.getPrincipal());
    }

    @GetMapping("/{communityId}/discussions/{discussionId}")
    public DiscussionDto getDiscussion(UsernamePasswordAuthenticationToken token,
                                       @PathVariable UUID communityId,
                                       @PathVariable UUID discussionId) {
        return communityService.getDiscussion(communityId, discussionId, (Long) token.getPrincipal());
    }

    @DeleteMapping("/{communityId}/discussions/{discussionId}")
    public void deleteDiscussion(UsernamePasswordAuthenticationToken token,
                                 @PathVariable UUID communityId,
                                 @PathVariable UUID discussionId) {
        communityService.deleteDiscussion(communityId, discussionId, (Long) token.getPrincipal());
    }






    @GetMapping("/publication-feed")
    public FeedResponse getPublicationFeed(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant cursor,
            @RequestParam(defaultValue = "20") int size,
            final UsernamePasswordAuthenticationToken token) {
        return communityService.getPublicationFeed(cursor, size, (Long) token.getPrincipal());
    }

    @GetMapping("/discussion-feed")
    public FeedDiscussionResponse getDiscussionFeed(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant cursor,
            @RequestParam(defaultValue = "20") int size,
            final UsernamePasswordAuthenticationToken token) {
        return communityService.getDiscussionFeed(cursor, size, (Long) token.getPrincipal());
    }





    @GetMapping("/{communityId}/discussions/{topicId}/comments")
    public FeedDiscussionResponse getDiscussionTopics(
            @PathVariable UUID communityId,
            @PathVariable UUID topicId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant cursor,
            @RequestParam(defaultValue = "20") int size,
            UsernamePasswordAuthenticationToken token) {
        return communityService.getDiscussionTopics(communityId, topicId, cursor, size, (Long) token.getPrincipal());
    }




    @GetMapping("/{communityId}/discussions/{parentId}/replies")
    public FeedDiscussionResponse getDiscussionReplies(
            @PathVariable UUID communityId,
            @PathVariable UUID parentId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant cursor,
            @RequestParam(defaultValue = "20") int size,
            UsernamePasswordAuthenticationToken token) {
        return communityService.getDiscussionReplies(communityId, parentId, cursor, size, (Long) token.getPrincipal());
    }
}