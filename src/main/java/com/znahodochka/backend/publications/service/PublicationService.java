package com.znahodochka.backend.publications.service;

import com.znahodochka.backend.publications.dto.CreatePublicationDto;
import com.znahodochka.backend.publications.dto.FeedResponse;
import com.znahodochka.backend.publications.dto.PublicationDto;
import com.znahodochka.backend.publications.dto.UpdatePublicationDto;
import com.znahodochka.backend.publications.model.PublicationType;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface PublicationService {
    PublicationDto createPublication(CreatePublicationDto dto, Long userId);

    PublicationDto getPublicationById(UUID id, Long userId);

    public FeedResponse getFeed(Instant cursor, int size, Long userId);

    FeedResponse getUserPublications(Long userId, PublicationType type, Instant cursor, int size);

    FeedResponse getChallengeResponses(UUID challengeId, Instant cursor, int size, Long userId);

    FeedResponse getFollowingFeed(Instant cursor, int size, Long userId);
    void deletePublication(UUID id, Long userId);

    PublicationDto updatePublication(UUID id, UpdatePublicationDto dto, Long userId);
    FeedResponse searchPublications(Long categoryId, Set<Long> tagIds,
                                    String region, String city,
                                    Instant cursor, int size, Long userId);

    List<String> getCities(String region);
}
