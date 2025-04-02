package com.social.postService.mapper;

import com.social.postService.dto.response.CommentResponse;
import com.social.postService.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class); // tao 1 instance singleTon-> k can inject khi xai
    @Mapping(source = "content",target = "content")
    @Mapping(source = "userPostInteraction.user",target = "user")
    CommentResponse toCommentResponse(Comment cmt);


    default List<CommentResponse> toCommentResponses(List<Comment> cmts) {
        if (cmts == null) return Collections.emptyList();
        return cmts.stream()
                .filter(cmt -> cmt.getContent() != null) // Lọc comment có content
                .map(this::toCommentResponse)
                .collect(Collectors.toList());
    }
}
