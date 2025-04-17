package com.social.postService.service.messageproducer;


import com.social.postService.dto.request.message.NotificationPostMessage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, NotificationPostMessage> kafkaTemplate;


    public KafkaProducerService(KafkaTemplate<String, NotificationPostMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    @Async
    public void sendPostMessage(String topic , NotificationPostMessage message) {
        try {
            kafkaTemplate.send(topic, message);
            System.out.println("Message sent to " + topic + ": " + message);
        } catch (Exception e) {
            System.err.println("Failed to send message to " + topic + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}

