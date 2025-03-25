package com.social.postService.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public enum MediaType {
    IMAGE,
    VIDEO,
    AUDIO,
    DOCUMENT,
    GIF,
    LIVE_STREAM,
    THUMBNAIL,
    ANIMATION
}
