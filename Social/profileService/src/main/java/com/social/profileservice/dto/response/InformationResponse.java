package com.social.profileservice.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InformationResponse {
     String id;
     String firstName;
     String lastName;
     Date dob;
     String avatar;
}
