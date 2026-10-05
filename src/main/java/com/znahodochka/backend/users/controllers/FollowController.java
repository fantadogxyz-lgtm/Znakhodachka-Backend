package com.znahodochka.backend.users.controllers;

import com.znahodochka.backend.users.dto.CreateFollowingDto;
import com.znahodochka.backend.users.dto.FollowingDto;
import com.znahodochka.backend.users.dto.ShortUserInfoDto;
import com.znahodochka.backend.users.services.FollowingService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/follow")
public class FollowController {
    private final FollowingService followingService;

    public FollowController(FollowingService followingService) {
        this.followingService = followingService;
    }

    //todO:
    // создать подписку,
    // удалить,
    // получить папищиков,
    // получить подписки

    @PostMapping()
    @PreAuthorize("hasAnyRole('BASIC')")
    public FollowingDto createFollowing(@RequestBody CreateFollowingDto dto,
                                        final UsernamePasswordAuthenticationToken token) {
    return followingService.createFollowing(dto, (Long) token.getPrincipal());
    }

    @DeleteMapping()
    @PreAuthorize("hasAnyRole('BASIC')")
    public void deleteFollowing(@RequestParam Long userId,
                                        final UsernamePasswordAuthenticationToken token) {
        followingService.deleteFollowing(userId, (Long) token.getPrincipal());
    }

    @GetMapping("/followers")
    @PreAuthorize("hasAnyRole('BASIC')")
    public List<ShortUserInfoDto> getFollowers(@RequestParam Long userId,
                                               final UsernamePasswordAuthenticationToken token) {
        return followingService.getFollowers(userId);
    }

    @GetMapping("/followings")
    @PreAuthorize("hasAnyRole('BASIC')")
    public List<ShortUserInfoDto> getFollowings(@RequestParam Long userId,
                                                final UsernamePasswordAuthenticationToken token) {
        return followingService.getFollowings(userId);
    }

    @GetMapping("/exists")
    @PreAuthorize("hasAnyRole('BASIC')")
    public boolean isFollowing(
            @RequestParam Long userId,
            final UsernamePasswordAuthenticationToken token) {
        return followingService.isFollowing((Long) token.getPrincipal(), userId);
    }
}
