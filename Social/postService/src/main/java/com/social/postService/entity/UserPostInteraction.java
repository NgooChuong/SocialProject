package com.social.postService.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "UserPostInteraction", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"userId", "postId"})
})
public class UserPostInteraction{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    Timestamp created_at;
    Timestamp updated_at;

    @PrePersist
    public void prePersist() {
        this.created_at = new Timestamp(System.currentTimeMillis());
        this.updated_at = new Timestamp(System.currentTimeMillis());
    }

    @PreUpdate
    public void preUpdate() {
        this.updated_at = new Timestamp(System.currentTimeMillis());
    }

    @ManyToOne
    @JoinColumn(name = "userId")
    User user;

    @ManyToOne
    @JoinColumn(name = "postId")
    Post post;

    // Quan hệ với bảng khác (ví dụ: ExpressStatus)
    @ManyToOne
    @JoinColumn(name = "reactionId")
    Reaction reaction;

    @ToString.Exclude
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "userPostInteraction")
    List<Comment> comments;
}
