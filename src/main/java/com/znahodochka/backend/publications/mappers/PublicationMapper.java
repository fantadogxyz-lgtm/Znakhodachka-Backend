package com.znahodochka.backend.publications.mappers;

import com.znahodochka.backend.config.DefaultMapperConf;
import com.znahodochka.backend.publications.dto.CategoryDto;
import com.znahodochka.backend.publications.dto.CreatePublicationDto;
import com.znahodochka.backend.publications.dto.PublicationDto;
import com.znahodochka.backend.publications.dto.TagDto;
import com.znahodochka.backend.publications.model.Category;
import com.znahodochka.backend.publications.model.Publication;
import com.znahodochka.backend.publications.model.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(config =  DefaultMapperConf.class)
public interface PublicationMapper {
    @Mapping(target = "type", source = "publicationType")
    @Mapping(target = "parentPublicationId", source = "parentPublication.id")
    @Mapping(target = "shortUserInfoDto", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "isChallengeActive", ignore = true)
    @Mapping(target = "isWinner", ignore = true)
    @Mapping(target = "isLiked", ignore = true)
    //@Mapping(target = "location", ignore = true)
    PublicationDto toDto(Publication publication);

    CategoryDto toCategoryDto(Category category);
    TagDto toTagDto(Tag tag);
    List<TagDto> toTagDtoList(Set<Tag> tags);
}
