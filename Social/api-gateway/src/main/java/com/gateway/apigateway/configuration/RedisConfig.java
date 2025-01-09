package com.gateway.apigateway.configuration;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {

    @Bean
    public RedisClient redisClient() {
        return RedisClient.create("redis://localhost:6379"); // Kết nối với Redis
    }

    @Bean
    public StatefulRedisConnection<String, String> connection(RedisClient redisClient) {
        return redisClient.connect(); // Thiết lập kết nối
    }

    @Bean
    public RedisCommands<String, String> syncCommands(StatefulRedisConnection<String, String> connection) {
        return connection.sync(); // Lấy ra các lệnh Redis đồng bộ
    }
}

