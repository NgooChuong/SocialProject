package com.social.postService.dto.response;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)

public class EmbeddingResponse {
    private Embedding embedding;

    @Data
    public static class Embedding {
        private List<Float> values;
    }
}
