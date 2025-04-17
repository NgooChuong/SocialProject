package com.social.postService.dto.request.message;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationPostMessage {
    UserNotiMessage sender;
    UserNotiMessage receiver;
    Timestamp createdAt;
    String postId;
}
