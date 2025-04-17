package com.social.notificationService.mapper;

import com.social.notificationService.dto.request.message.UserMessage;
import com.social.notificationService.dto.response.NotiResponse;
import com.social.notificationService.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper()
public interface NotiMapper {
    NotiMapper INSTANCE = Mappers.getMapper(NotiMapper.class);

    @Mapping(target = "status", source = "notification.status")
    @Mapping(target = "sender", ignore = true)
    @Mapping(target = "createdAt", expression = "java(notification.getCreatedAt() != null ? new java.sql.Timestamp(notification.getCreatedAt().getTime()) : null)")
    @Mapping(target = "id", source = "notification.id") // Ánh xạ id (không cần expression nếu tên giống nhau)
    NotiResponse toNotiResponse(Notification notification);
     default NotiResponse toNotiResponse(Notification notification, UserMessage sender) {
        NotiResponse response = toNotiResponse(notification);
        response.setSender(sender);
        return response;
    }
}
