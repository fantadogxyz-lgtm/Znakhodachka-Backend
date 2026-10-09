package com.znahodochka.backend.publications.controller;

import com.znahodochka.backend.publications.dto.*;
import com.znahodochka.backend.publications.model.PublicationType;
import com.znahodochka.backend.publications.repositories.CategoryRepository;
import com.znahodochka.backend.publications.repositories.TagRepository;
import com.znahodochka.backend.publications.service.PublicationService;
import com.znahodochka.backend.users.dto.LoginDto;
import com.znahodochka.backend.users.dto.LoginResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/publication")
@RequiredArgsConstructor
public class PublicationController {

    private final PublicationService publicationService;
    private final CategoryRepository categoryRepository;
    private  final TagRepository tagRepository;

    @PostMapping()
    @PreAuthorize("hasAnyRole('BASIC')")
    public PublicationDto createPublication(@RequestBody final CreatePublicationDto dto,
                                            final UsernamePasswordAuthenticationToken token) {
       return publicationService.createPublication(dto, (Long)token.getPrincipal());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('BASIC')")
    public PublicationDto getPublication(@PathVariable final UUID id,
                                         final UsernamePasswordAuthenticationToken token) {
        return publicationService.getPublicationById(id, (Long)token.getPrincipal());
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('BASIC')")
    public FeedResponse getUserPublications(
            @PathVariable Long userId,
            @RequestParam(required = false) PublicationType type,
            @RequestParam(required = false) Instant cursor,
            @RequestParam(defaultValue = "20") int size) {
        return publicationService.getUserPublications(userId, type, cursor, size);
    }

    @GetMapping("/feed")
    @PreAuthorize("hasAnyRole('BASIC')")
    public FeedResponse getFeed(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant cursor,
            @RequestParam(defaultValue = "20") int size,
            final UsernamePasswordAuthenticationToken token) {
        return publicationService.getFeed(cursor, size, (Long)token.getPrincipal());
    }

    @GetMapping("/feed/following")
    @PreAuthorize("hasAnyRole('BASIC')")
    public FeedResponse getFollowingFeed(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant cursor,
            @RequestParam(defaultValue = "20") int size,
            final UsernamePasswordAuthenticationToken token) {
        return publicationService.getFollowingFeed(cursor, size, (Long) token.getPrincipal());
    }

    @GetMapping("/categories")
    @PreAuthorize("hasAnyRole('BASIC')")
    public List<CategoryDto> getCategories() {
        return categoryRepository.findAll().stream()
                .map(c -> new CategoryDto(c.getId(), c.getTitle()))
                .toList();
    }

    @GetMapping("/tags")
    @PreAuthorize("hasAnyRole('BASIC')")
    public List<TagDto> getTags() {
        return tagRepository.findAll().stream()
                .map(t -> new TagDto(t.getId(), t.getTitle()))
                .toList();
    }

    @GetMapping("/{challengeId}/responses")
    @PreAuthorize("hasAnyRole('BASIC')")
    public FeedResponse getChallengeResponses(
            @PathVariable UUID challengeId,
            @RequestParam(required = false) Instant cursor,
            @RequestParam(defaultValue = "20") int size,
            final UsernamePasswordAuthenticationToken token) {
        return publicationService.getChallengeResponses(challengeId, cursor, size, (Long) token.getPrincipal());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('BASIC')")
    public void deletePublication(@PathVariable UUID id,
                                  final UsernamePasswordAuthenticationToken token) {
        publicationService.deletePublication(id, (Long) token.getPrincipal());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('BASIC')")
    public void updatePublication(@PathVariable UUID id,
                                  @RequestBody final UpdatePublicationDto dto,
                                  final UsernamePasswordAuthenticationToken token) {
        publicationService.updatePublication(id, dto, (Long) token.getPrincipal());
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('BASIC')")
    public FeedResponse searchPublications(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String city,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant cursor,
            @RequestParam(defaultValue = "20") int size,
            final UsernamePasswordAuthenticationToken token) {
        return publicationService.searchPublications(
                categoryId, null, region, city, cursor, size, (Long) token.getPrincipal());
    }

    @GetMapping("/cities")
    @PreAuthorize("hasAnyRole('BASIC')")
    public List<String> getCities(@RequestParam(required = false) String region) {
        return publicationService.getCities(region);
    }

    @GetMapping("/search-map")
    @PreAuthorize("hasAnyRole('BASIC')")
    public List<PublicationDto> searchPublications(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam double radiusKm,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Set<Long> tagIds,
            final UsernamePasswordAuthenticationToken token){
        double radiusMeters = radiusKm * 1000;
        return publicationService.getRandomPublicationsInRadius(lat, lon, radiusMeters, categoryId, tagIds, (Long)token.getPrincipal());
    }

}
