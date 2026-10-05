package com.znahodochka.backend.publications.controller;

import com.znahodochka.backend.publications.service.LikeService;
import com.znahodochka.backend.users.dto.ShortUserInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/publication/{publicationId}/like")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping()
    @PreAuthorize("hasAnyRole('BASIC')")
    public void like(@PathVariable UUID publicationId,
                     final UsernamePasswordAuthenticationToken token) {
        likeService.like(publicationId, (Long) token.getPrincipal());
    }

    @DeleteMapping()
    @PreAuthorize("hasAnyRole('BASIC')")
    public void unlike(@PathVariable UUID publicationId,
                       final UsernamePasswordAuthenticationToken token) {
        likeService.unlike(publicationId, (Long) token.getPrincipal());
    }

    @GetMapping("/users")
    @PreAuthorize("hasAnyRole('BASIC')")
    public List<ShortUserInfoDto> getLikes(@PathVariable UUID publicationId) {
        return likeService.getUsersWhoLiked(publicationId);
    }
}
