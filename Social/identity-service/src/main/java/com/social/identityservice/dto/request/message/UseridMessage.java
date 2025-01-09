package com.social.identityservice.dto.request.message;

import lombok.*;
import lombok.experimental.FieldDefaults;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UseridMessage {
    Long userid;
    String email;
}
