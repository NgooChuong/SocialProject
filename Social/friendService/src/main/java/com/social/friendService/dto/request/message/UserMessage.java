package com.social.friendService.dto.request.message;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserMessage {
    String id;
    String username;
    String avatar;
}
