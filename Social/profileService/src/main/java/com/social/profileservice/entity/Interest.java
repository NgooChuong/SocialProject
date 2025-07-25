package com.social.profileservice.entity;

import com.social.profileservice.enums.InterestCategory;
import com.social.profileservice.enums.InterestType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.Set;

// 1. Interest Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(
        name = "interests",
        indexes = {
                @Index(name = "idx_category", columnList = "category"),
                @Index(name = "idx_name", columnList = "name")
        }
)
public class Interest extends Base{


    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    InterestType name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    InterestCategory category;

    @Column(columnDefinition = "TEXT")
    String description;

    @Builder.Default
    @Column(nullable = false)
    Boolean isActive = true;
    // Relationship with UserInterest
    @OneToMany(mappedBy = "interest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Set<UserInterest> userInterests;
}
