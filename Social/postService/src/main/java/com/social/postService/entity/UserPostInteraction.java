package com.social.postService.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "UserPostInteraction", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"userId", "postId"})
})
public class UserPostInteraction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "userId")
    User user;

    @ManyToOne
    @JoinColumn(name = "postId")
    Post post;

    // Quan hệ với bảng khác (ví dụ: ExpressStatus)
    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "reactionId")
    Reaction reaction;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "userPostInteraction")
    List<Comment> comments;
}
