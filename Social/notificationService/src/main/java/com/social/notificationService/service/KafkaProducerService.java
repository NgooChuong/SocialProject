package com.social.notificationService.service;


import com.social.notificationService.dto.request.message.NotiFriendMessage;
import com.social.notificationService.dto.request.message.NotificationPostMessage;
import com.social.notificationService.dto.request.message.NotificationWithUsers;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC_FRIEND = "friend_topic";
    private static final String TOPIC_POST = "Post_topic";

    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendFriendMessage(NotiFriendMessage message) {
        sendMessage(TOPIC_FRIEND, message);
    }

    public void sendPostMessage(NotificationPostMessage message) {
        sendMessage(TOPIC_POST, message);
    }

    private void sendMessage(String topic, Object message) {
        try {
            kafkaTemplate.send(topic, message).get();
            System.out.println("Message sent to " + topic + ": " + message);
        } catch (Exception e) {
            System.err.println("Failed to send message to " + topic + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}

