package com.znahodochka.backend.publications.service;

import com.znahodochka.backend.exceptions.BadRequestException;
import com.znahodochka.backend.exceptions.BusinessExceptionCode;
import com.znahodochka.backend.exceptions.ResourceNotFoundException;
import com.znahodochka.backend.publications.model.Like;
import com.znahodochka.backend.publications.model.Publication;
import com.znahodochka.backend.publications.model.PublicationType;
import com.znahodochka.backend.publications.repositories.LikeRepository;
import com.znahodochka.backend.publications.repositories.PublicationRepository;
import com.znahodochka.backend.users.dto.ShortUserInfoDto;
import com.znahodochka.backend.users.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.znahodochka.backend.publications.model.PublicationType.*;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
    private final LikeRepository likeRepository;
    private final PublicationRepository publicationRepository;
    private final UserService userService;
    @Override
    @Transactional
    public void like(UUID publicationId, Long userId) {
        Publication publication = publicationRepository.findById(publicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Publication with id: " + publicationId + " not found"));

        if (publication.getPublicationType() == CHALLENGE_RESPONSE) {
            Publication parent = publication.getParentPublication();
            if (parent != null && parent.getEndDate() != null
                    && parent.getEndDate().isBefore(Instant.now())) {
                throw new BadRequestException(BusinessExceptionCode.CHALLENGE_FINISHED);
            }
        }

        if (likeRepository.existsByPublicationIdAndAuthorId(publicationId, userId)) {
            throw new BadRequestException(BusinessExceptionCode.ALREADY_LIKED);
        }

        Like like = Like.builder()
                .publicationId(publicationId)
                .authorId(userId)
                .build();
        likeRepository.save(like);

        publicationRepository.incrementLikesCount(publicationId);
    }

    @Override
    @Transactional
    public void unlike(UUID publicationId, Long userId) {
        Publication publication = publicationRepository.findById(publicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Publication with id: " + publicationId + " not found"));

        if (publication.getPublicationType() == CHALLENGE_RESPONSE) {
            Publication parent = publication.getParentPublication();
            if (parent != null && parent.getEndDate() != null
                    && parent.getEndDate().isBefore(Instant.now())) {
                throw new BadRequestException(BusinessExceptionCode.CHALLENGE_FINISHED);
            }
        }

        if (!likeRepository.existsByPublicationIdAndAuthorId(publicationId, userId)) {
            throw new BadRequestException(BusinessExceptionCode.ALREADY_DISLIKED);
        }

        Like like = likeRepository.findByPublicationIdAndAuthorId(publicationId, userId).orElseThrow(ResourceNotFoundException::new);
        likeRepository.delete(like);

        publicationRepository.decrementLikesCount(publicationId);
    }


    @Override
    @Transactional
    public List<ShortUserInfoDto> getUsersWhoLiked(UUID publicationId) {
        Publication publication = publicationRepository.findById(publicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Publication with id: " + publicationId + " not found"));
        return likeRepository.findAllByPublicationId(publicationId).stream()
                .map(like ->  {
                    return userService.getShortUserInfo(like.getAuthorId());
                }).toList();
    }
}
