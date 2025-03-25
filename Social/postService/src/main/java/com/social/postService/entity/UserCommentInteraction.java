package com.social.postService.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "UserCommentInteraction", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"userId", "commentId","likeId"})
})
public class UserCommentInteraction extends Base{
    @ManyToOne
    @JoinColumn(name = "userId")
    User user;

    @ManyToOne
    @JoinColumn(name = "commentId")
    Comment comment;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "reactionId")
    Reaction reaction;
}
