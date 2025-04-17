package com.social.notificationService.dto.request.message;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationPostMessage implements Serializable {
    UserNotiMessage sender;
    UserNotiMessage receiver;
    Timestamp createdAt;
    String postId;
}
