package com.social.postService.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment extends Base {
    String content;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userPostInteractionId")
    UserPostInteraction userPostInteraction;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.REMOVE)
    List<UserCommentInteraction> userExpresses;
}
