package com.znahodochka.backend.publications.service;

import com.znahodochka.backend.exceptions.BadRequestException;
import com.znahodochka.backend.exceptions.BusinessExceptionCode;
import com.znahodochka.backend.exceptions.ResourceNotFoundException;
import com.znahodochka.backend.publications.dto.*;
import com.znahodochka.backend.publications.model.*;
import com.znahodochka.backend.publications.repositories.*;
import com.znahodochka.backend.storage.dto.DiscussionAttachmentDto;
import com.znahodochka.backend.storage.repositories.DiscussionAttachmentRepository;
import com.znahodochka.backend.users.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.znahodochka.backend.publications.mappers.PublicationMapper;
import com.znahodochka.backend.storage.dto.AttachmentDto;
import com.znahodochka.backend.storage.service.AttachmentService;
import com.znahodochka.backend.users.dto.ShortUserInfoDto;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CommunityServiceImpl implements CommunityService {

    private static final int MAX_COMMUNITIES_PER_USER = 3;
    private static final int MAX_TAGS = 5;

    private final CommunityRepository communityRepository;
    private final CommunityTagRepository communityTagRepository;
    private final UserService userService;
    private final CommunityParticipationRepository communityParticipationRepository;
    private final DiscussionRepository discussionRepository;
    private final DiscussionLikeRepository discussionLikeRepository;
    private final DiscussionAttachmentRepository discussionAttachmentRepository;
    private final PublicationRepository publicationRepository;
    private final PublicationMapper publicationMapper;
    private final AttachmentService attachmentService;
    private final LikeRepository likeRepository;
    private final GeolocationRepository geolocationRepository;

    @Override
    public CommunityDto createCommunity(CreateCommunityDto dto, Long creatorId) {
        if (communityRepository.countByCreatorId(creatorId) >= MAX_COMMUNITIES_PER_USER) {
            throw new BadRequestException(BusinessExceptionCode.COMMUNITY_LIMIT_REACHED);
        }
        if (communityRepository.findBySlug(dto.slug()).isPresent()) {
            throw new BadRequestException(BusinessExceptionCode.SLUG_ALREADY_TAKEN);
        }
        if (dto.tags() == null || dto.tags().isEmpty()) {
            throw new BadRequestException(BusinessExceptionCode.MIN_ONE_TAG_REQUIRED);
        }
        if (dto.tags().size() > MAX_TAGS) {
            throw new BadRequestException(BusinessExceptionCode.TOO_MANY_TAGS);
        }

        Community community = Community.builder()
                .slug(dto.slug())
                .title(dto.title())
                .description(dto.description())
                .creatorId(creatorId)
                .coverUri(dto.coverUri())
                .build();
        community = communityRepository.save(community);

        for (String tagTitle : dto.tags()) {
            communityTagRepository.save(CommunityTag.builder()
                    .communityId(community.getId())
                    .title(tagTitle)
                    .build());
        }

        communityParticipationRepository.save(
                CommunityParticipation.builder()
                        .communityId(community.getId())
                        .userId(creatorId)
                        .build()
        );

        return toDto(community, creatorId);
    }

    @Override
    public CommunityDto updateCommunity(UUID id, UpdateCommunityDto dto, Long userId) {
        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Community not found"));

        if (!community.getCreatorId().equals(userId)) {
            throw new BadRequestException(BusinessExceptionCode.NOT_YOUR_COMMUNITY);
        }
        if (dto.slug() != null && !dto.slug().equals(community.getSlug())) {
            if (communityRepository.findBySlug(dto.slug()).isPresent()) {
                throw new BadRequestException(BusinessExceptionCode.SLUG_ALREADY_TAKEN);
            }
            community.setSlug(dto.slug());
        }
        if (dto.title() != null) community.setTitle(dto.title());
        if (dto.description() != null) community.setDescription(dto.description());

        return toDto(communityRepository.save(community), userId);
    }

    @Override
    public void deleteCommunity(UUID id, Long userId) {
        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Community not found"));
        if (!community.getCreatorId().equals(userId)) {
            throw new BadRequestException(BusinessExceptionCode.NOT_YOUR_COMMUNITY);
        }
        communityRepository.delete(community);
    }

    @Override
    @Transactional(readOnly = true)
    public CommunityDto getCommunityBySlug(String slug, Long currentUserId) {
        Community community = communityRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Community not found"));
        return toDto(community, currentUserId);
    }

    @Override
    public CommunityTagDto addTag(UUID communityId, String title, Long userId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new ResourceNotFoundException("Community not found"));
        if (!community.getCreatorId().equals(userId)) {
            throw new BadRequestException(BusinessExceptionCode.NOT_YOUR_COMMUNITY);
        }
        if (communityTagRepository.countByCommunityId(communityId) >= MAX_TAGS) {
            throw new BadRequestException(BusinessExceptionCode.TOO_MANY_TAGS);
        }
        if (communityTagRepository.existsByCommunityIdAndTitle(communityId, title)) {
            throw new BadRequestException(BusinessExceptionCode.TAG_ALREADY_EXISTS);
        }

        CommunityTag tag = communityTagRepository.save(CommunityTag.builder()
                .communityId(communityId)
                .title(title)
                .build());

        return new CommunityTagDto(tag.getId(), tag.getTitle());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommunityTagDto> getTags(UUID communityId) {
        return communityTagRepository.findByCommunityId(communityId).stream()
                .map(t -> new CommunityTagDto(t.getId(), t.getTitle()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommunityShortDto> getUserCommunities(Long userId, Long currentUserId) {
        return communityRepository.findUserCommunities(userId).stream()
                .map(c -> new CommunityShortDto(
                        c.getId(),
                        c.getSlug(),
                        c.getTitle(),
                        c.getCoverUri(),
                        c.getMembersCount(),
                        c.getCreatorId().equals(userId)
                ))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommunityShortDto> searchCommunities(String query, Long currentUserId) {
        if (query == null || query.trim().length() < 2) {
            return List.of();
        }
        return communityRepository.searchBySlugOrTitle(query.trim())
                .stream()
                .map(c -> new CommunityShortDto(
                        c.getId(),
                        c.getSlug(),
                        c.getTitle(),
                        c.getCoverUri(),
                        c.getMembersCount(),
                        c.getCreatorId().equals(currentUserId)
                ))
                .toList();
    }

    @Override
    public void updateCoverUri(UUID communityId, String coverUri, Long userId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new ResourceNotFoundException("Community not found"));

        if (!community.getCreatorId().equals(userId)) {
            throw new BadRequestException(BusinessExceptionCode.NOT_YOUR_COMMUNITY);
        }

        community.setCoverUri(coverUri);
        communityRepository.save(community);
    }

    @Override
    public void deleteCoverUri(UUID communityId, Long userId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new ResourceNotFoundException("Community not found"));

        if (!community.getCreatorId().equals(userId)) {
            throw new BadRequestException(BusinessExceptionCode.NOT_YOUR_COMMUNITY);
        }

        community.setCoverUri(null);
        communityRepository.save(community);
    }

    @Override
    public boolean isSlugFree(String slug) {
        return !communityRepository.existsBySlug(slug);
    }

    @Override
    @Transactional
    public DiscussionDto createDiscussion(CreateDiscussionDto dto, UUID id, Long principal) {
        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Community not found"));

        if (!communityParticipationRepository.existsByCommunityIdAndUserId(id, principal)) {
            throw new BadRequestException(BusinessExceptionCode.NOT_YOUR_COMMUNITY);
        }

        Discussion discussion = Discussion.builder()
                .text(dto.text())
                .type(dto.type())
                .authorId(principal)
                .mentionedUserId(dto.mentionedUserId())
                .community(community)
                .build();

        if (dto.topic_id() != null) {
            Discussion topic = discussionRepository.findById(dto.topic_id())
                    .orElseThrow(() -> new BadRequestException(BusinessExceptionCode.DISCUSSION_TOPIC_NOT_FOUND));
            discussion.setTopic(topic);
        }

        if (dto.parent_id() != null) {
            Discussion parent = discussionRepository.findById(dto.parent_id())
                    .orElseThrow(() -> new BadRequestException(BusinessExceptionCode.DISCUSSION_PARENT_NOT_FOUND));
            discussion.setParent(parent);
        }

        Discussion saved = discussionRepository.save(discussion);


        if (dto.attachments() != null && !dto.attachments().isEmpty()) {
            List<UUID> attachmentIds = dto.attachments().stream()
                    .map(DiscussionAttachmentDto::id)
                    .toList();
            attachmentService.attachToDiscussion(attachmentIds, saved.getId());
        }


        if (dto.geolocations() != null && !dto.geolocations().isEmpty()) {
            for (CreateGeolocationDto geoDto : dto.geolocations()) {
                Geolocation geo = Geolocation.builder()
                        .title(geoDto.title())
                        .latitude(geoDto.latitude())
                        .longitude(geoDto.longitude())
                        .discussion(saved)
                        .build();
                if (geoDto.latitude() != null && geoDto.longitude() != null) {
                    geo.setLocation(makePoint(geoDto.longitude(), geoDto.latitude()));
                }
                geolocationRepository.save(geo);
            }
        }

        return toDiscussionDto(saved, principal);
    }

    @Override
    @Transactional
    public DiscussionDto updateDiscussion(UpdateDiscussionDto dto, UUID communityId, UUID discussionId, Long principal) {
        if (!communityParticipationRepository.existsByCommunityIdAndUserId(communityId, principal)) {
            throw new BadRequestException(BusinessExceptionCode.NOT_YOUR_COMMUNITY);
        }

        Discussion discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new ResourceNotFoundException("Discussion not found"));

        if (!discussion.getAuthorId().equals(principal)) {
            throw new BadRequestException(BusinessExceptionCode.NOT_YOUR_DISCUSSION);
        }

        discussion.setText(dto.text());


        if (dto.geolocations() != null) {
            discussion.getGeolocations().clear();

            for (CreateGeolocationDto geoDto : dto.geolocations()) {
                Geolocation geo = Geolocation.builder()
                        .title(geoDto.title())
                        .latitude(geoDto.latitude())
                        .longitude(geoDto.longitude())
                        .discussion(discussion)
                        .build();
                if (geoDto.latitude() != null && geoDto.longitude() != null) {
                    geo.setLocation(makePoint(geoDto.longitude(), geoDto.latitude()));
                }
                discussion.getGeolocations().add(geo);
            }
        }

        Discussion saved = discussionRepository.save(discussion);
        return toDiscussionDto(saved, principal);
    }

    @Override
    @Transactional(readOnly = true)
    public DiscussionDto getDiscussion(UUID communityId, UUID discussionId, Long principal) {

        Discussion discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new ResourceNotFoundException("Discussion not found"));

        return toDiscussionDto(discussion, principal);
    }

    @Override
    @Transactional
    public void deleteDiscussion(UUID communityId, UUID discussionId, Long principal) {
        if (!communityParticipationRepository.existsByCommunityIdAndUserId(communityId, principal)) {
            throw new BadRequestException(BusinessExceptionCode.NOT_YOUR_COMMUNITY);
        }

        Discussion discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new ResourceNotFoundException("Discussion not found"));

        if (!discussion.getAuthorId().equals(principal)) {
            throw new BadRequestException(BusinessExceptionCode.NOT_YOUR_DISCUSSION);
        }

        List<Discussion> replies = discussionRepository.findAllByParentId(discussionId);
        if (!replies.isEmpty()) {
            discussionRepository.deleteAllInBatch(replies);
        }
        discussionRepository.delete(discussion);
    }

    @Override
    @Transactional(readOnly = true)
    public FeedResponse getPublicationFeed(Instant cursor, int size, Long principal) {
        Pageable pageable = PageRequest.of(0, size + 1);
        List<Publication> publications;

        if (cursor == null) {
            publications = publicationRepository.findCommunityFeedFirstPage(PublicationType.CHALLENGE_RESPONSE, pageable);
        } else {
            publications = publicationRepository.findCommunityFeedNextPage(PublicationType.CHALLENGE_RESPONSE, cursor, pageable);
        }

        boolean hasMore = publications.size() > size;
        if (hasMore) publications = publications.subList(0, size);

        List<PublicationDto> items = publications.stream().map(p -> toCommunityFeedDto(p, principal)).toList();
        Instant nextCursor = publications.isEmpty() ? null : publications.get(publications.size() - 1).getCreatedAt();

        return FeedResponse.builder().items(items).nextCursor(nextCursor).hasMore(hasMore).build();
    }

    @Override
    @Transactional(readOnly = true)
    public FeedDiscussionResponse getDiscussionFeed(Instant cursor, int size, Long principal) {
        Pageable pageable = PageRequest.of(0, size + 1);
        List<Discussion> discussions = (cursor == null)
                ? discussionRepository.findCommunityFeedFirstPage(pageable)
                : discussionRepository.findCommunityFeedNextPage(cursor, pageable);
        return buildDiscussionFeed(discussions, size, principal);
    }

    @Override
    @Transactional(readOnly = true)
    public FeedDiscussionResponse getDiscussionTopics(UUID communityId, UUID topicId, Instant cursor, int size, Long principal) {
        Pageable pageable = PageRequest.of(0, size + 1);
        List<Discussion> discussions = (cursor == null)
                ? discussionRepository.findTopicFirstPage(communityId, topicId, pageable)
                : discussionRepository.findTopicNextPage(communityId, topicId, cursor, pageable);
        return buildDiscussionFeed(discussions, size, principal);
    }

    @Override
    @Transactional(readOnly = true)
    public FeedDiscussionResponse getDiscussionReplies(UUID communityId, UUID parentId, Instant cursor, int size, Long principal) {
        Pageable pageable = PageRequest.of(0, size + 1);
        List<Discussion> discussions = (cursor == null)
                ? discussionRepository.findRepliesFirstPage(communityId, parentId, pageable)
                : discussionRepository.findRepliesNextPage(communityId, parentId, cursor, pageable);
        return buildDiscussionFeed(discussions, size, principal);
    }

    private FeedDiscussionResponse buildDiscussionFeed(List<Discussion> raw, int size, Long principal) {
        boolean hasMore = raw.size() > size;
        List<Discussion> page = hasMore ? raw.subList(0, size) : raw;
        Instant nextCursor = page.isEmpty() ? null : page.get(page.size() - 1).getCreatedAt();

        return FeedDiscussionResponse.builder()
                .items(page.stream().map(d -> toDiscussionDto(d, principal)).toList())
                .nextCursor(nextCursor)
                .hasMore(hasMore)
                .build();
    }

    private DiscussionDto toDiscussionDto(Discussion d, Long principal) {
        List<DiscussionAttachmentDto> attachments = discussionAttachmentRepository
                .findAllByDiscussionId(d.getId())
                .stream()
                .map(a -> DiscussionAttachmentDto.builder()
                        .id(a.getId())
                        .fileUri(a.getFileUri())
                        .photoOrder(a.getPhotoOrder())
                        .type(a.getType())
                        .build())
                .toList();

        ShortUserInfoDto mentioned = d.getMentionedUserId() != null
                ? userService.getShortUserInfo(d.getMentionedUserId())
                : null;

        boolean isLiked = principal != null && discussionLikeRepository.existsByDiscussionIdAndAuthorId(d.getId(), principal);

        List<GeolocationDto> geos = d.getGeolocations() != null
                ? d.getGeolocations().stream()
                .map(g -> new GeolocationDto(g.getId(), g.getTitle(), g.getLatitude(), g.getLongitude()))
                .toList()
                : List.of();

        return DiscussionDto.builder()
                .created_at(d.getCreatedAt())
                .id(d.getId())
                .text(d.getText())
                .type(d.getType())
                .author_id(d.getAuthorId())
                .isAuthor(d.getAuthorId().equals(principal))
                .parent_id(d.getParent() != null ? d.getParent().getId() : null)
                .topic_id(d.getTopic() != null ? d.getTopic().getId() : null)
                .community_id(d.getCommunity() != null ? d.getCommunity().getId() : null)
                .attachments(attachments)
                .mentionedUser(mentioned)
                .countOfLikes(d.getCountOfLikes())
                .isLiked(isLiked)
                .geolocations(geos)
                .build();
    }

    private CommunityDto toDto(Community c, Long currentUserId) {
        long membersCount = c.getMembersCount();
        List<CommunityTagDto> tags = communityTagRepository.findByCommunityId(c.getId())
                .stream().map(t -> new CommunityTagDto(t.getId(), t.getTitle())).toList();

        boolean isMember = currentUserId != null && communityParticipationRepository.existsByCommunityIdAndUserId(c.getId(), currentUserId);
        boolean isCreator = c.getCreatorId().equals(currentUserId);

        return CommunityDto.builder()
                .id(c.getId()).slug(c.getSlug()).title(c.getTitle()).description(c.getDescription())
                .coverUri(c.getCoverUri()).creator(userService.getShortUserInfo(c.getCreatorId()))
                .membersCount(membersCount).tags(tags).isMember(isMember).isCreator(isCreator).createdAt(c.getCreatedAt())
                .build();
    }

    private PublicationDto toCommunityFeedDto(Publication publication, Long currentUserId) {
        PublicationDto dto = publicationMapper.toDto(publication);
        ShortUserInfoDto author = userService.getShortUserInfo(publication.getAuthorId());
        List<AttachmentDto> attachments = attachmentService.getByPublicationId(publication.getId());

        Boolean isLiked = currentUserId != null && likeRepository.existsByPublicationIdAndAuthorId(publication.getId(), currentUserId);
        Boolean isActive = null;
        if (publication.getPublicationType() == PublicationType.CHALLENGE) {
            isActive = publication.getEndDate() != null && publication.getEndDate().isAfter(Instant.now());
        } else if (publication.getPublicationType() == PublicationType.CHALLENGE_RESPONSE) {
            Publication parent = publication.getParentPublication();
            isActive = parent != null && parent.getEndDate() != null && parent.getEndDate().isAfter(Instant.now());
        }

        Boolean isWinner = false;
        if (publication.getPublicationType() == PublicationType.CHALLENGE_RESPONSE) {
            Publication parent = publication.getParentPublication();
            if (parent != null && parent.getEndDate() != null && parent.getEndDate().isBefore(Instant.now())
                    && publication.getLikesCount() != null && publication.getLikesCount() > 0) {
                Long maxLikes = publicationRepository.findMaxLikesForChallenge(parent.getId(), PublicationType.CHALLENGE_RESPONSE);
                isWinner = publication.getLikesCount().equals(maxLikes);
            }
        }

        return dto.toBuilder().shortUserInfoDto(author).isChallengeActive(isActive).attachments(attachments).isWinner(isWinner).isLiked(isLiked).build();
    }


    private Point makePoint(Double lon, Double lat) {
        GeometryFactory gf = new GeometryFactory(new PrecisionModel(), 4326);
        return gf.createPoint(new Coordinate(lon, lat));
    }
}