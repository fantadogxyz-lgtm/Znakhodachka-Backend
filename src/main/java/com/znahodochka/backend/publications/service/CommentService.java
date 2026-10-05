package com.znahodochka.backend.publications.service;

import com.znahodochka.backend.publications.dto.CommentDto;
import com.znahodochka.backend.publications.dto.CreateCommentDto;

import java.util.List;
import java.util.UUID;

public interface CommentService {
    CommentDto createComment(UUID publicationId, CreateCommentDto dto, Long userId);

    List<CommentDto> getRootComments(UUID publicationId);

    List<CommentDto> getReplies(UUID commentId, int page, int size);

    CommentDto updateComment(UUID commentId, String content, Long userId);

    void deleteComment(UUID commentId, Long userId);
}
