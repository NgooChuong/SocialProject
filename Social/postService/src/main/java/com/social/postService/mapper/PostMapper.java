package com.social.postService.mapper;

import com.social.postService.dto.response.PostResponse;
import com.social.postService.entity.Picture;
import com.social.postService.entity.Post;
import com.social.postService.entity.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface PostMapper {
    PostMapper INSTANCE = Mappers.getMapper(PostMapper.class); // tao 1 instance singleTon-> k can inject khi xai
//    source 🡆 tên thuộc tính của đối tượng nguồn (trong Post).
//    target 🡆 tên thuộc tính của đối tượng đích (trong PostResponse).
//    qualifiedByName 🡆 hàm ánh xạ tùy chỉnh, dùng nếu cần chuyển đổi dữ liệu.
    @Mapping(source = "tags", target = "tags", qualifiedByName = "mapTagsToNames")
    @Mapping(source = "pics", target = "pics", qualifiedByName = "mapPicsToUrls")
    @Mapping(source = "user", target = "user")
    PostResponse toPostResponse(Post post);

    @Named("mapTagsToNames")
    static List<String> mapTagsToNames(List<Tag> tags) {
        return tags != null ? tags.stream().map(Tag::getName).collect(Collectors.toList()) : null;
    }

    @Named("mapPicsToUrls")
    static List<String> mapPicsToUrls(List<Picture> pics) {
        return pics != null ? pics.stream().map(Picture::getUrl).collect(Collectors.toList()) : null;
    }
}
