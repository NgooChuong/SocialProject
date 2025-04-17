package com.social.friendService.dto.request.message;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationFriendMessage {
    UserMessage sender;
    UserMessage receiver;
    Timestamp createdAt;
}
