package com.znahodochka.backend.publications.service;

import com.znahodochka.backend.exceptions.BadRequestException;
import com.znahodochka.backend.exceptions.BusinessExceptionCode;
import com.znahodochka.backend.exceptions.ResourceNotFoundException;
import com.znahodochka.backend.publications.dto.*;
import com.znahodochka.backend.publications.mappers.PublicationMapper;
import com.znahodochka.backend.publications.model.Publication;
import com.znahodochka.backend.publications.model.PublicationType;
import com.znahodochka.backend.publications.repositories.CategoryRepository;
import com.znahodochka.backend.publications.repositories.LikeRepository;
import com.znahodochka.backend.publications.repositories.PublicationRepository;
import com.znahodochka.backend.publications.repositories.TagRepository;
import com.znahodochka.backend.storage.dto.AttachmentDto;
import com.znahodochka.backend.storage.service.AttachmentService;
import com.znahodochka.backend.users.dto.ShortUserInfoDto;
import com.znahodochka.backend.users.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PublicationServiceImpl implements PublicationService {
    private final UserService userService;
    private final PublicationMapper publicationMapper;
    private final PublicationRepository publicationRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final AttachmentService attachmentService;
    private final LikeRepository likeRepository;

    @Override
    public PublicationDto createPublication(CreatePublicationDto dto, Long userId) {
        validateByType(dto);

        Publication publication = Publication.builder()
                .authorId(userId)
                .publicationType(dto.type())
                .title(dto.title())
                .description(dto.description())
                .latitude(dto.latitude())
                .longitude(dto.longitude())
                .region(dto.region())
                .city(dto.city())
                .endDate(dto.endDate())
                .category(categoryRepository.findById(dto.category()).orElseThrow(ResourceNotFoundException::new))
                .tags(new HashSet<>(tagRepository.findAllById(dto.tags())))
                .build();


        if (dto.type() == PublicationType.CHALLENGE_RESPONSE) {
            Publication parent = publicationRepository.findById(dto.parentPublicationId())
                    .orElseThrow(ResourceNotFoundException::new);
            if (parent.getPublicationType() != PublicationType.CHALLENGE) {
                throw new BadRequestException(BusinessExceptionCode.INVALID_PARENT);
            }
            publication.setParentPublication(parent);
            publication.setTitle(parent.getTitle());
            publication.setDescription(parent.getDescription());
            publication.setLatitude(parent.getLatitude());
            publication.setLongitude(parent.getLongitude());
            publication.setRegion(parent.getRegion());
            publication.setCity(parent.getCity());
            publication.setCategory(parent.getCategory());
            publication.setTags(new HashSet<>(parent.getTags()));
        }

        Publication saved = publicationRepository.save(publication);

        if (dto.attachmentIds() != null && !dto.attachmentIds().isEmpty()) {
            attachmentService.attachToPublication(dto.attachmentIds(), saved.getId());
        }

        return toDtoWithAuthor(saved, userId);
    }

    @Override
    public PublicationDto getPublicationById(UUID id, Long userId) {
        Publication publication = publicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Publication with id: " + id + " not found"));
        return toDtoWithAuthor(publication, userId);
    }

    @Override
    public FeedResponse getFeed(Instant cursor, int size, Long userId) {
        Pageable pageable = PageRequest.of(0, size+1);
        List<Publication> publications;
        if(cursor == null) {
            publications = publicationRepository.findFirstPage(PublicationType.CHALLENGE_RESPONSE, pageable);
        } else{
            publications = publicationRepository.findNextPage(cursor, PublicationType.CHALLENGE_RESPONSE, pageable);
        }

        boolean hasMore = publications.size() > size;

        if (hasMore) {
            publications = publications.subList(0, size);
        }

        List<PublicationDto> items = publications.stream()
                .map(p -> toDtoWithAuthor(p, userId))
                .toList();

        Instant nextCursor = publications.isEmpty()
                ? null
                : publications.get(publications.size() - 1).getCreatedAt();


        return FeedResponse.builder()
                .items(items)
                .nextCursor(nextCursor)
                .hasMore(hasMore)
                .build();
    }

    @Override
    public FeedResponse getUserPublications(Long userId, PublicationType type, Instant cursor, int size) {
        Pageable pageable = PageRequest.of(0, size + 1);
        List<Publication> publications;
        if (cursor == null) {
            publications = publicationRepository.findUserFirstPage(userId, type, pageable);
        } else {
            publications = publicationRepository.findUserNextPage(userId, type, cursor, pageable);
        }

        boolean hasMore = publications.size() > size;
        if (hasMore) {
            publications = publications.subList(0, size);
        }

        List<PublicationDto> items = publications.stream()
                .map(p -> toDtoWithAuthor(p, userId))
                .toList();

        Instant nextCursor = publications.isEmpty()
                ? null
                : publications.get(publications.size() - 1).getCreatedAt();

        return FeedResponse.builder()
                .items(items)
                .nextCursor(nextCursor)
                .hasMore(hasMore)
                .build();
    }

    @Override
    public FeedResponse getChallengeResponses(UUID challengeId, Instant cursor, int size, Long userId) {
        Pageable pageable = PageRequest.of(0, size+1);
        List<Publication> publications;
        if(cursor == null) {
            publications = publicationRepository.findResponsesByChallengeId(challengeId, pageable);
        } else{
            publications = publicationRepository.findResponsesNextPage(challengeId, cursor, pageable);
        }

        boolean hasMore = publications.size() > size;

        if (hasMore) {
            publications = publications.subList(0, size);
        }

        List<PublicationDto> items = publications.stream()
                .map(p -> toDtoWithAuthor(p, userId))
                .toList();

        Instant nextCursor = publications.isEmpty()
                ? null
                : publications.get(publications.size() - 1).getCreatedAt();


        return FeedResponse.builder()
                .items(items)
                .nextCursor(nextCursor)
                .hasMore(hasMore)
                .build();
    }

    @Override
    public FeedResponse getFollowingFeed(Instant cursor, int size, Long userId) {
        Pageable pageable = PageRequest.of(0, size + 1);
        List<Publication> publications;


        if (cursor == null) {
            publications = publicationRepository.findFollowingFirstPage(
                    userId, PublicationType.CHALLENGE_RESPONSE, pageable);
        } else {
            publications = publicationRepository.findFollowingNextPage(
                    userId, PublicationType.CHALLENGE_RESPONSE, cursor, pageable);
        }

        boolean hasMore = publications.size() > size;
        if (hasMore) {
            publications = publications.subList(0, size);
        }

        List<PublicationDto> items = publications.stream()
                .map(p -> toDtoWithAuthor(p, userId))
                .toList();

        Instant nextCursor = publications.isEmpty()
                ? null
                : publications.get(publications.size() - 1).getCreatedAt();

        return FeedResponse.builder()
                .items(items)
                .nextCursor(nextCursor)
                .hasMore(hasMore)
                .build();
    }

    @Override
    public void deletePublication(UUID id, Long userId) {
        Publication publication = publicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Publication not found"));

        if (!publication.getAuthorId().equals(userId)) {
            throw new BadRequestException(BusinessExceptionCode.NOT_YOUR_PUBLICATION);
        }

        publicationRepository.delete(publication);
    }

    private PublicationDto toDtoWithAuthor(Publication publication, Long currentUserId) {
        PublicationDto dto = publicationMapper.toDto(publication);
        ShortUserInfoDto author = userService.getShortUserInfo(publication.getAuthorId());
        List<AttachmentDto> attachments = attachmentService.getByPublicationId(publication.getId());

        Boolean isLiked = false;
        if (currentUserId != null) {
            isLiked = likeRepository.existsByPublicationIdAndAuthorId(publication.getId(), currentUserId);
        }

        Boolean isActive = null;
        if (publication.getPublicationType() == PublicationType.CHALLENGE) {
            isActive = publication.getEndDate() != null
                    && publication.getEndDate().isAfter(Instant.now());
        } else if (publication.getPublicationType() == PublicationType.CHALLENGE_RESPONSE) {
            Publication parent = publication.getParentPublication();
            isActive = parent != null
                    && parent.getEndDate() != null
                    && parent.getEndDate().isAfter(Instant.now());
        }


        Boolean isWinner = false;
        if (publication.getPublicationType() == PublicationType.CHALLENGE_RESPONSE) {
            Publication parent = publication.getParentPublication();
            if (parent != null
                    && parent.getEndDate() != null
                    && parent.getEndDate().isBefore(Instant.now())
                    && publication.getLikesCount() != null
                    && publication.getLikesCount() > 0) {
                Long maxLikes = publicationRepository.findMaxLikesForChallenge(parent.getId(), PublicationType.CHALLENGE_RESPONSE);
                isWinner = publication.getLikesCount().equals(maxLikes);
            }
        }

        return dto.toBuilder()
                .shortUserInfoDto(author)
                .isChallengeActive(isActive)
                .attachments(attachments)
                .isWinner(isWinner)
                .isLiked(isLiked)
                .build();
    }

    private void validateByType(CreatePublicationDto dto) {
        switch (dto.type()) {
            case POST -> {
                if (dto.attachmentIds() == null || dto.attachmentIds().isEmpty() || dto.attachmentIds().size() > 15) {
                    throw new BadRequestException(BusinessExceptionCode.INVALID_ATTACHMENT_COUNT);
                }
                if (dto.title() == null || dto.title().isBlank()) {
                    throw new BadRequestException(BusinessExceptionCode.INVALID_TITLE);
                }
                if (dto.latitude() == null || dto.longitude() == null) {
                    throw new BadRequestException(BusinessExceptionCode.INVALID_COORDINATES);
                }
                if (dto.category() == null) {
                    throw new BadRequestException(BusinessExceptionCode.INVALID_CATEGORY);
                }
            }
            case CHALLENGE -> {
                if (dto.attachmentIds() == null || dto.attachmentIds().size() != 1) {
                    throw new BadRequestException(BusinessExceptionCode.CHALLENGE_MUST_HAVE_ONE_PHOTO);
                }
                if (dto.endDate() == null) {
                    throw new BadRequestException(BusinessExceptionCode.CHALLENGE_MUST_HAVE_END_DATE);
                }
                if (dto.parentPublicationId() != null) {
                    throw new BadRequestException(BusinessExceptionCode.CHALLENGE_CANNOT_HAVE_PARENT);
                }
            }
            case CHALLENGE_RESPONSE -> {
                if (dto.attachmentIds() == null || dto.attachmentIds().size() != 1) {
                    throw new BadRequestException(BusinessExceptionCode.RESPONSE_MUST_HAVE_ONE_PHOTO);
                }
                if (dto.parentPublicationId() == null) {
                    throw new BadRequestException(BusinessExceptionCode.RESPONSE_MUST_HAVE_PARENT);
                }
            }
        }
        }

    @Transactional
    @Override
    public PublicationDto updatePublication(UUID id, UpdatePublicationDto dto, Long userId) {
        Publication publication = publicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Publication not found"));

        if (!publication.getAuthorId().equals(userId)) {
            throw new BadRequestException(BusinessExceptionCode.NOT_YOUR_PUBLICATION);
        }

        if (dto.title() != null && !dto.title().isBlank()) publication.setTitle(dto.title());
        if (dto.description() != null) publication.setDescription(dto.description());
        if (dto.categoryId() != null) {
            publication.setCategory(categoryRepository.findById(dto.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found")));
        }
        if (dto.tagIds() != null) {
            publication.setTags(new HashSet<>(tagRepository.findAllById(dto.tagIds())));
        }
        if (dto.endDate() != null && publication.getPublicationType() == PublicationType.CHALLENGE) {
            publication.setEndDate(dto.endDate());
        }

        Publication saved = publicationRepository.save(publication);

        return toDtoWithAuthor(saved, userId);
    }

    @Override
    public FeedResponse searchPublications(Long categoryId, Set<Long> tagIds,
                                           String region, String city,
                                           Instant cursor, int size, Long userId) {
        Pageable pageable = PageRequest.of(0, size + 1);
        Instant cursorValue = cursor != null ? cursor : Instant.now().plusSeconds(1);

        List<Publication> publications = publicationRepository.findFilteredPageWithLocation(
                PublicationType.CHALLENGE_RESPONSE, categoryId, region, city, cursorValue, pageable);

        boolean hasMore = publications.size() > size;
        if (hasMore) publications = publications.subList(0, size);

        List<PublicationDto> items = publications.stream()
                .map(p -> toDtoWithAuthor(p, userId))
                .toList();

        Instant nextCursor = publications.isEmpty()
                ? null
                : publications.get(publications.size() - 1).getCreatedAt();

        return FeedResponse.builder()
                .items(items)
                .nextCursor(nextCursor)
                .hasMore(hasMore)
                .build();
    }

    @Override
    public List<String> getCities(String region) {
        return publicationRepository.findDistinctCities(region);
    }

    @Override
    public List<PublicationDto> getRandomPublicationsInRadius(double lat, double lon, double radiusMeters, Long CategoryId, Set<Long> tagIds, Long currentUserId) {
        List<Publication> publications;
        if (tagIds == null || CategoryId == null) {
            publications = publicationRepository.findRandomInRadius(lat, lon, radiusMeters);
        } else if (tagIds == null || CategoryId != null) {
            publications = publicationRepository.findRandomInRadiusWithCategory(
                    lat, lon, radiusMeters, CategoryId);
        } else{
            publications = publicationRepository.findRandomInRadiusWithCategoryAndTags(
                    lat, lon, radiusMeters, CategoryId, tagIds);
        }

        return publications.stream()
                .map(publication -> toDtoWithAuthor(publication, currentUserId)).toList();
    }
}
