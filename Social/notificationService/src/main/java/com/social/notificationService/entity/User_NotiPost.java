package com.social.notificationService.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Data
@Document(collection = "UserNotiPost")
public class User_NotiPost {
    @DBRef
    User receiver;
    @DBRef
    Notification notificationPost;
}
