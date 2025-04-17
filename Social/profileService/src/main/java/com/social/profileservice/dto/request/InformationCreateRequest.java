package com.social.profileservice.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InformationCreateRequest {
    String userId;
    String firstName;
    String lastName;
    Date dob;
    String avatar;
}
