package com.social.profileservice.dto.response;

import com.social.profileservice.enums.InterestType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;

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
     Double score;
     String location;
     List<InterestType> interests;

}
