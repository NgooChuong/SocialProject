package com.social.identityservice.mapper;

import com.social.identityservice.dto.request.UserCreationRequest;
import com.social.identityservice.dto.request.UserUpdateRequest;
import com.social.identityservice.dto.response.UserResponse;
import com.social.identityservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);

    UserResponse toUserResponse(User user);

    void updateUser(@MappingTarget User user, UserUpdateRequest request);

}
