package com.social.notificationService.dto.request.message;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.sql.Timestamp;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class NotificationWithUsers {
    UserMessage sender;
    UserMessage receiver;
    Timestamp createdAt;
}
