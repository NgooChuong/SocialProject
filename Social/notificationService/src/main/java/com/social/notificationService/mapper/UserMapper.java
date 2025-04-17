package com.social.notificationService.mapper;


import com.social.notificationService.dto.request.message.UserMessage;
import com.social.notificationService.dto.request.message.UserNotiMessage;
import com.social.notificationService.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
    User toUser(UserNotiMessage user);
    UserMessage toUserMessage(User user);

}
