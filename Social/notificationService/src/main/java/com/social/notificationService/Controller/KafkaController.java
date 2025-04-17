package com.social.notificationService.Controller;

import com.social.notificationService.dto.request.message.NotiFriendMessage;
import com.social.notificationService.dto.request.message.NotificationPostMessage;
import com.social.notificationService.service.KafkaProducerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KafkaController {

    private final KafkaProducerService kafkaProducerService;

    public KafkaController(KafkaProducerService kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
    }

    @GetMapping("/send")
    public String sendMessage(@RequestBody NotiFriendMessage message) {
        kafkaProducerService.sendFriendMessage(message);
        return "Message sent successfully";
    }
    @GetMapping("/sendPost")
    public String sendMessage(@RequestBody NotificationPostMessage message) {
        kafkaProducerService.sendPostMessage(message);
        return "Message sent successfully";
    }
}
