package com.social.friendService.service.messageproducer;


import com.social.friendService.dto.request.message.NotificationFriendMessage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, NotificationFriendMessage> kafkaTemplate;


    public KafkaProducerService(KafkaTemplate<String, NotificationFriendMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    @Async
    public void sendFriendMessage(String topic , NotificationFriendMessage message) {
        try {
            kafkaTemplate.send(topic, message);
            System.out.println("Message sent to " + topic + ": " + message);
        } catch (Exception e) {
            System.err.println("Failed to send message to " + topic + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}

