package com.znahodochka.backend.publications.service;

import com.znahodochka.backend.exceptions.BadRequestException;
import com.znahodochka.backend.exceptions.BusinessExceptionCode;
import com.znahodochka.backend.exceptions.ResourceNotFoundException;
import com.znahodochka.backend.publications.dto.CommentDto;
import com.znahodochka.backend.publications.dto.CreateCommentDto;
import com.znahodochka.backend.publications.mappers.CommentMapper;
import com.znahodochka.backend.publications.model.Comment;
import com.znahodochka.backend.publications.repositories.CommentRepository;
import com.znahodochka.backend.publications.repositories.PublicationRepository;
import com.znahodochka.backend.users.dto.ShortUserInfoDto;
import com.znahodochka.backend.users.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PublicationRepository publicationRepository;
    private final CommentMapper commentMapper;
    private final UserService userService;

    @Override
    @Transactional
    public CommentDto createComment(UUID publicationId, CreateCommentDto dto, Long userId) {
        publicationRepository.findById(publicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Publication with id: " + publicationId + " not found"));

        if (dto.content() == null || dto.content().isBlank()) {
            throw new BadRequestException(BusinessExceptionCode.INVALID_COMMENT_CONTENT);
        }

        Comment comment = Comment.builder()
                .authorId(userId)
                .mentionedUserId(dto.mentionedUserId())
                .publicationId(publicationId)
                .content(dto.content())
                .build();

        if (dto.parentId() != null) {
            Comment parent = commentRepository.findById(dto.parentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent comment not found"));

            if (!parent.getPublicationId().equals(publicationId)) {
                throw new BadRequestException(BusinessExceptionCode.INVALID_PARENT);
            }
            comment.setParent(parent);
        }
        Comment saved = commentRepository.save(comment);

        publicationRepository.incrementCommentsCount(publicationId);

        return toDtoWithAuthor(saved);
    }

    @Override
    public List<CommentDto> getRootComments(UUID publicationId) {
        return commentRepository
                .findAllByPublicationIdAndParentIsNullOrderByCreatedAtDesc(publicationId)
                .stream()
                .map(this::toDtoWithAuthorAndCount)
                .toList();
    }

    @Override
    public List<CommentDto> getReplies(UUID commentId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return commentRepository
                .findAllByParentIdOrderByCreatedAtAsc(commentId, pageable)
                .stream()
                .map(this::toDtoWithAuthor)
                .toList();
    }

    @Override
    @Transactional
    public CommentDto updateComment(UUID commentId, String content, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        if (!comment.getAuthorId().equals(userId)) {
            throw new BadRequestException(BusinessExceptionCode.NOT_YOUR_COMMENT);
        }

        if (content == null || content.isBlank()) {
            throw new BadRequestException(BusinessExceptionCode.INVALID_COMMENT_CONTENT);
        }

        comment.setContent(content);
        Comment saved = commentRepository.save(comment);
        return toDtoWithAuthor(saved);
    }

    @Override
    @Transactional
    public void deleteComment(UUID commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        if (!comment.getAuthorId().equals(userId)) {
            throw new BadRequestException(BusinessExceptionCode.NOT_YOUR_COMMENT);
        }

        if (comment.getParent() == null) {
            List<Comment> replies = commentRepository.findAllByParentIdOrderByCreatedAtAsc(commentId);
            long totalToDelete = 1 + replies.size();
            commentRepository.deleteAllInBatch(replies);
            commentRepository.delete(comment);
            publicationRepository.decrementCommentsCountBy(comment.getPublicationId(), totalToDelete);
        } else {
            commentRepository.delete(comment);
            publicationRepository.decrementCommentsCount(comment.getPublicationId());
        }
    }


    private CommentDto toDtoWithAuthorAndCount(Comment comment) {
        CommentDto dto = commentMapper.toDto(comment);
        ShortUserInfoDto author = userService.getShortUserInfo(comment.getAuthorId());
        long repliesCount = commentRepository.countByParentId(comment.getId());
        return dto.toBuilder()
                .author(author)
                .repliesCount(repliesCount)
                .build();
    }

    private CommentDto toDtoWithAuthor(Comment comment) {
        CommentDto dto = commentMapper.toDto(comment);
        ShortUserInfoDto author = userService.getShortUserInfo(comment.getAuthorId());
        ShortUserInfoDto mentioned = comment.getMentionedUserId() != null
                ? userService.getShortUserInfo(comment.getMentionedUserId())
                : null;
        return dto.toBuilder()
                .author(author)
                .mentionedUser(mentioned)
                .repliesCount(0L)
                .build();
    }
}
