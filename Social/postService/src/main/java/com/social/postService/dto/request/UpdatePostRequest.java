package com.social.postService.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdatePostRequest {
    String title;
    String content;
    List<MultipartFile> pictures;
    Boolean isPrivate;
    String mediaType;
    List<String> tags;
}
