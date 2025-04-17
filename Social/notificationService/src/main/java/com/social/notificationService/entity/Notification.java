package com.social.notificationService.entity;


import com.social.notificationService.enums.NotiStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Date;


@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class Notification {
    @Id
    String id;
    @Indexed(unique = true)
    Date createdAt;
    String title;
    String content;
    NotiStatus status;
}
