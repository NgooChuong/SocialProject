package com.social.postService.entity;

import com.social.postService.enums.MediaType;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post extends Base {
    String title;
    String content;
    Boolean isPrivate;
    @Enumerated(EnumType.STRING) // ORDINAL is store as number form in db
    MediaType mediaType;

    @ManyToOne
    @ToString.Exclude // ⚠️ Tránh vòng lặp vô hạn
    @JoinColumn(name="userId")
    User user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    List<UserPostInteraction> userExpresses;

    @ManyToMany
    @JoinTable(
            name = "post_tag",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"post_id", "tag_id"})
    )
    List<Tag> tags;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    List<Picture> pics;
}
