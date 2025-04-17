package com.social.notificationService.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "NotificationFriend")
public class NotificationFriend extends Notification {
    @DBRef
    @Indexed
    User sender;
    @DBRef
    @Indexed
    User receiver;
}
