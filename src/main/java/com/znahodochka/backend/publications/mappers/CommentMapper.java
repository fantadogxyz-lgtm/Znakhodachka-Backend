package com.znahodochka.backend.publications.mappers;

import com.znahodochka.backend.config.DefaultMapperConf;
import com.znahodochka.backend.publications.dto.CommentDto;
import com.znahodochka.backend.publications.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;

@Mapper(config =  DefaultMapperConf.class)
public interface CommentMapper {
    @Mapping(target = "parentId", source = "parent.id")
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "replies", ignore = true)
    @Mapping(target = "mentionedUser", ignore = true)
    CommentDto toDto(Comment comment);
}
