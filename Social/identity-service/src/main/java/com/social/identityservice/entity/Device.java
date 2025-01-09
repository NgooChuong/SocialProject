package com.social.identityservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Device {
    @Id
    String deviceId;
    @OneToOne
    @JoinColumn( referencedColumnName = "userId", nullable = false)
    User userId;
    String deviceToken;
    String deviceType;
    String operatingSystem;
    Date lastActiveAt;
    Date createdAt;
    Date updatedAt;
    Date deletedAt;
}
