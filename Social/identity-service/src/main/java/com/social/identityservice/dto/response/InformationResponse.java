package com.social.identityservice.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InformationResponse {
     String username;
     String firstName;
     String lastName;
     String email;
     String phone;
     String role;
     String avatar;
}
