package com.social.notificationService.dto.request.message;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserNotiMessage {
    String id;
    String username;
    String avatar;
}
