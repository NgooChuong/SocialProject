package com.social.identityservice.dto.redis;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RedisResponse {
    String token;
    String refreshToken;
}
