package com.social.identityservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
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
    // Cập nhật dữ liệu trong Redis với key và object
    public <T> void update(String key, T object) throws JsonProcessingException {
        // Chuyển đối tượng thành JSON string
        String jsonValue = objectMapper.writeValueAsString(object);

        // Kiểm tra xem dữ liệu có tồn tại không
        String currentValue = syncCommands.get(key);  // Lấy giá trị hiện tại từ Redis
        log.info("currentValue: {}", currentValue);
        // Nếu giá trị không tồn tại, cập nhật dữ liệu vào Redis
        syncCommands.set(key, jsonValue);  // Cập nhật lại giá trị cho key

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
