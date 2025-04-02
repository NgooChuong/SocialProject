package com.social.postService.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@Data
public class Picture extends Base {
    @ManyToOne
    @JoinColumn(name="postId")
    Post post;
    String url;
}
