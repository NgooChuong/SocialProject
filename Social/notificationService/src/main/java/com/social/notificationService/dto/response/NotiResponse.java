package com.social.notificationService.dto.response;

import com.social.notificationService.dto.request.message.UserMessage;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotiResponse {
    String id;
    String title;
    String content;
    UserMessage sender;
    Timestamp createdAt;
    String status;
}
