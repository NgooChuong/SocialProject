package com.social.postService.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

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
     List<String> interests;
}
