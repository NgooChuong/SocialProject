package com.social.postService.dto.request.message;

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
