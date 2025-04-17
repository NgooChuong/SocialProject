package com.social.profileservice.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InformationUpdateRequest {
    String userId;
    String firstName;
    String lastName;
    Date dob;
    String avatar;
    MultipartFile file;
}
