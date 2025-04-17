package com.social.notificationService.dto.request.message;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class NotiFriendMessage implements Serializable {
    UserNotiMessage sender;
    UserNotiMessage receiver;
    Timestamp createdAt;
}
