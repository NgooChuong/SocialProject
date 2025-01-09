package com.social.identityservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String refreshToken;
    String userId;
    Long expiresAt;  // Thời gian hết hạn của Refresh Token

    boolean isRevoked;  // Trạng thái Refresh Token đã bị thu hồi hay chưa

    Date createdAt;  // Thời gian tạo Refresh Token

}
