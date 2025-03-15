package com.social.identityservice.mapper;

import com.social.identityservice.entity.Information;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    Information toUser(UserCreationRequest request);

    UserResponse toUserResponse(Information user);

    void updateUser(@MappingTarget Information user, UserUpdateRequest request);

}
