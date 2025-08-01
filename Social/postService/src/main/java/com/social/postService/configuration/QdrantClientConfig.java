package com.social.postService.configuration;

import io.grpc.Grpc;
import io.grpc.TlsChannelCredentials;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.io.File;
import java.io.IOException;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QdrantClientConfig {
    @Value("${qdrant.port}")
    Integer port;

    @Value("${qdrant.endpoint}")
    String host;

    @Value("${qdrant.api.key}")
    String apiKey;


    @Bean
    public QdrantClient qdrantClient() {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 6334)
                .usePlaintext() // 👈 KHÔNG dùng TLS
                .build();
        return new QdrantClient(
                QdrantGrpcClient.newBuilder(channel).withApiKey(apiKey).build()
        );
    }
}
