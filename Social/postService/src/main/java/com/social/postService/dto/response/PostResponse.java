package com.social.postService.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostResponse {
    String id;
    String title;
    String content;
    Boolean isPrivate;
    List<String> tags;
    List<String> pics;
    UserResponse user;
    Integer countLikes;
    Integer countComments;
    ReactionResponse myReaction;
    Timestamp createdAt;
    Timestamp updatedAt;
}
