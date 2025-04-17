package com.social.notificationService.service;
import com.social.notificationService.dto.request.message.NotiFriendMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    public void consume(NotiFriendMessage message) {
        System.out.println("Message received: " + message);
    }
}