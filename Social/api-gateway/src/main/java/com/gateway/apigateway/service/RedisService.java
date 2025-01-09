package com.gateway.apigateway.service;

import io.lettuce.core.api.sync.RedisCommands;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

    @Autowired
    private RedisCommands<String, String> syncCommands; // Sử dụng Lettuce

    public void save(String key, String value) {
        syncCommands.set(key, value); // Lưu giá trị vào Redis
    }

    public String get(String key) {
        return syncCommands.get(key); // Lấy giá trị từ Redis
    }

    public void delete(String key) {
        syncCommands.del(key); // Xóa giá trị khỏi Redis
    }
}
