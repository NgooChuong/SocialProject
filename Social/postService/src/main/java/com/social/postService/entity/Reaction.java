package com.social.postService.entity;

import com.social.postService.enums.ReactionType;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@Data
public class Reaction extends Base{
    @Enumerated(EnumType.STRING) // ORDINAL is store as number form in db
    ReactionType iconName;
    @OneToMany(mappedBy = "reaction")
    List<UserPostInteraction> userPostInteraction;

    @OneToOne(mappedBy = "reaction")
    UserCommentInteraction userCommentInteraction;
}
