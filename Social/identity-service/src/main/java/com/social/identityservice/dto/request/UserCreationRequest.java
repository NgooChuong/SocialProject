package com.social.identityservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    @Size(min = 3,message = "USERNAME_INVALID")
    String username;
    @Size(min = 8, message = "INVALID_PASSWORD")
    String password;
    @Email (message = "INVALID_EMAIL")
    String email;
    @Size(min = 10, max =11 , message = "INVALID_PHONE")
    String phone;
    String firstName;
    String lastName;
    Date dob;
    MultipartFile file;
    String login_at;
    String google_id;
    String location;
}


