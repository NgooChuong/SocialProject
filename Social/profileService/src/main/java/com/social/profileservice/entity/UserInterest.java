package com.social.profileservice.entity;
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
@Table(
        name = "user_interests",
        indexes = {
                @Index(name = "idx_user_id", columnList = "userId"),
                @Index(name = "idx_interest_id", columnList = "interestId"),
                @Index(name = "idx_user_interest", columnList = "userId, interestId")
        },
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"userId", "interestId"})
        }
)
public class UserInterest extends Base{

    @Column(nullable = false)
    String userId;

    @Column(nullable = false)
    String interestId;


    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interestId", insertable = false, updatable = false)
    Interest interest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    Information userInformation;
}
