package com.social.postService.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;


@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    String id;
    String username;
    String avatar;
    @ToString.Exclude // ⚠️ Tránh vòng lặp vô hạn
    @OneToMany(mappedBy = "user",cascade = CascadeType.REMOVE, targetEntity = Post.class)
    List<Post> posts;

    @ToString.Exclude // ⚠️ Tránh vòng lặp vô hạn
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    List<UserPostInteraction> postExpresses;

    @ToString.Exclude // ⚠️ Tránh vòng lặp vô hạn
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    List<UserCommentInteraction> cmtExpresses;
}
