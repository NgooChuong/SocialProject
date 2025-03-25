package com.social.postService.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@Data
public class Reaction extends Base{
    String iconName;
    @OneToOne(mappedBy = "reaction")
    UserPostInteraction userPostInteraction;

    @OneToOne(mappedBy = "reaction")
    UserCommentInteraction userCommentInteraction;
}
