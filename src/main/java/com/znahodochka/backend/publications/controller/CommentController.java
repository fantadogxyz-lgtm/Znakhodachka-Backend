package com.znahodochka.backend.publications.controller;

import com.znahodochka.backend.publications.dto.CommentDto;
import com.znahodochka.backend.publications.dto.CreateCommentDto;
import com.znahodochka.backend.publications.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/publication/{publicationId}/comment")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping()
    @PreAuthorize("hasAnyRole('BASIC')")
    public CommentDto createComment(
            @PathVariable UUID publicationId,
            @RequestBody CreateCommentDto dto,
            final UsernamePasswordAuthenticationToken token) {
        return commentService.createComment(publicationId, dto, (Long) token.getPrincipal());
    }

    @GetMapping()
    @PreAuthorize("hasAnyRole('BASIC')")
    public List<CommentDto> getComments(@PathVariable UUID publicationId) {
        return commentService.getRootComments(publicationId);
    }

    @GetMapping("/{commentId}/replies")
    @PreAuthorize("hasAnyRole('BASIC')")
    public List<CommentDto> getReplies(
            @PathVariable UUID publicationId,
            @PathVariable UUID commentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return commentService.getReplies(commentId, page, size);
    }

    @PutMapping("/{commentId}")
    @PreAuthorize("hasAnyRole('BASIC')")
    public CommentDto updateComment(
            @PathVariable UUID publicationId,
            @PathVariable UUID commentId,
            @RequestParam String content,
            final UsernamePasswordAuthenticationToken token) {
        return commentService.updateComment(commentId, content, (Long) token.getPrincipal());
    }

    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasAnyRole('BASIC')")
    public void deleteComment(
            @PathVariable UUID publicationId,
            @PathVariable UUID commentId,
            final UsernamePasswordAuthenticationToken token) {
        commentService.deleteComment(commentId, (Long) token.getPrincipal());
    }
}
