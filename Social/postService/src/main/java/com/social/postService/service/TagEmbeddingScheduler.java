package com.social.postService.service;
import com.social.postService.dto.response.EmbeddingResponse;
import com.social.postService.entity.Tag;
import com.social.postService.repository.TagRepository;
import com.social.postService.repository.httpClient.GoogleEmbeddingClient;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Points;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

import static io.qdrant.client.PointIdFactory.id;
import static io.qdrant.client.VectorsFactory.vectors;
import static io.qdrant.client.ValueFactory.value;

@Slf4j
@Service
@RequiredArgsConstructor
public class TagEmbeddingScheduler {

    private final TagRepository tagRepository;
    private final GoogleEmbeddingClient embeddingClient;
    private final QdrantClient qdrantClient;

    private static final String TAG_COLLECTION_NAME = "tag-collection";

    /**
     * Chạy định kỳ mỗi ngày lúc 2:00 AM
     * Bạn có thể thay đổi cron theo nhu cầu.
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void indexTagEmbeddingsToQdrant() {
        try {
            log.info("🚀 Starting tag embedding indexing...");

            List<Tag> allTags = tagRepository.findAll();
            List<Points.PointStruct> tagPoints = new ArrayList<>();

            for (Tag tag : allTags) {
                EmbeddingResponse tagEmbeddingRes = embeddingClient.embed(tag.getName());
                List<Float> tagVector = tagEmbeddingRes.getEmbedding().getValues();

                Points.PointStruct tagPoint = Points.PointStruct.newBuilder()
                        .setId(id(UUID.randomUUID()))
                        .setVectors(vectors(tagVector))
                        .putAllPayload(Map.of(
                                "tag_id", value(tag.getId()),
                                "tag_name", value(tag.getName())
                        ))
                        .build();

                tagPoints.add(tagPoint);
            }

            Points.UpdateResult updateResult = qdrantClient
                    .upsertAsync(TAG_COLLECTION_NAME, tagPoints)
                    .get();

            log.info("✅ Tag embedding indexing complete. Result: {}", updateResult);

        } catch (Exception e) {
            log.error("🔥 Failed to index tag embeddings", e);
        }
    }
}

