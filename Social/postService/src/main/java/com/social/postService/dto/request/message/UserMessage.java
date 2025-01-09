package com.social.postService.dto.request.message;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserMessage {
    String username;
    Boolean active;
    String password;
    String role;
    Long userid;
}
