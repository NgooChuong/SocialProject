package com.social.postService.repository.httpClient;

import com.social.postService.dto.response.EmbeddingResponse;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GoogleEmbeddingClient {
    @Autowired
    RestTemplate restTemplate;
    @Value("${google.api.key}")
    String apiKey;
    @Value("${google.url}")
    String google_url;
    @Value("${google.url}")
    String embedding;

    public EmbeddingResponse embed(String text) {
        String url = google_url + apiKey;

        Map<String, Object> content = Map.of("parts", List.of(Map.of("text", text)));
        Map<String, Object> payload = Map.of("model", embedding, "content", content);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        ResponseEntity<EmbeddingResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                EmbeddingResponse.class
        );

        return response.getBody();
    }
}
