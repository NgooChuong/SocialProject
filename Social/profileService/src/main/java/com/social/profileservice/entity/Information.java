package com.social.profileservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(
        indexes = {
                @Index(name = "idx_user_id", columnList = "userId")
        }
)
public class Information {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String userId;
    String firstName;
    String lastName;
    Date dob;
    String avatar;
    String location;
    @Transient
    private MultipartFile file;
    @OneToMany(mappedBy = "userInformation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Set<UserInterest> userInterests;
}
