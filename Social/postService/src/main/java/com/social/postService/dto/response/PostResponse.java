package com.social.postService.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostResponse {
    String title;
    String content;
    Boolean isPrivate;
    List<String> tags;
    List<String> pics;
    UserResponse user;
    Integer countLikes;
    Integer countComments;
}
