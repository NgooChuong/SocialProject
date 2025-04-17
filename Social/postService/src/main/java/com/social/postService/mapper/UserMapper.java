package com.social.postService.mapper;
import com.social.postService.dto.response.UserResponse;
import com.social.postService.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
    @Mapping(source = "id", target = "id")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "avatar", target = "avatar")
    User toUser (UserResponse userResponse);

}
