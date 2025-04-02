package com.social.postService.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserReactionResponse {
    UserResponse user;
    Timestamp created_at;
    Timestamp updated_at;
    String reaction;

}
