package com.social.identityservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.lettuce.core.api.sync.RedisCommands;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

    @Autowired
    private RedisCommands<String, String> syncCommands; // Sử dụng Lettuce

    private final ObjectMapper objectMapper;

    // Constructor khởi tạo ObjectMapper
    public RedisService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // Tùy chỉnh (nếu cần)
    }

    // Lưu object vào Redis dưới dạng JSON
    public <T> void save(String key, T object) throws JsonProcessingException {
        String jsonValue = objectMapper.writeValueAsString(object); // Chuyển object thành JSON
        syncCommands.set(key, jsonValue); // Lưu JSON vào Redis
    }

    // Lấy object từ Redis và chuyển đổi về class tương ứng
    public <T> T get(String key, Class<T> clazz) throws JsonProcessingException {
        String jsonValue = syncCommands.get(key); // Lấy JSON từ Redis
        if (jsonValue != null) {
            return objectMapper.readValue(jsonValue, clazz); // Deserialize JSON thành object
        }
        return null;
    }

    // Xóa object khỏi Redis
    public void delete(String key) {
        syncCommands.del(key); // Xóa giá trị khỏi Redis
    }
}
