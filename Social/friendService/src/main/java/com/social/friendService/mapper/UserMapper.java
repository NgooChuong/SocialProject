package com.social.friendService.mapper;

import com.social.friendService.dto.response.UserResponse;
import com.social.friendService.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "avatar", target = "avatar")
    UserResponse toUserResponse(User user);
}
