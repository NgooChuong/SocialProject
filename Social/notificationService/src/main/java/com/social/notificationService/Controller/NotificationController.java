package com.social.notificationService.Controller;

import com.social.notificationService.dto.request.ApiResponse;
import com.social.notificationService.dto.request.message.NotiFriendMessage;
import com.social.notificationService.dto.request.message.NotificationPostMessage;
import com.social.notificationService.service.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationController {
    NotificationService notificationService;
    SimpMessagingTemplate messagingTemplate;

    @GetMapping(value = "/{receiverId}")
    public ApiResponse<?> getNotifications(@PathVariable String receiverId,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size,
                                           @RequestParam(defaultValue = "ALL") String type) {
        return ApiResponse.builder()
                .result(
                        notificationService.getNotifications(receiverId, type, page, size)
                )
                .build();
    }

    @KafkaListener(topics = "${kafka.friend_topic}",
            groupId = "${spring.kafka.consumer.group-id}",
            properties = {"spring.json.value.default.type=com.social.notificationService.dto.request.message.NotiFriendMessage"}
    )
    @MessageMapping("/noti.sendAddFriendNoti")
    public void addFriendNotification(NotiFriendMessage notiMessage) {
        log.info("NotiFriendMessage: {}", notiMessage);
        notificationService.store(notiMessage);
        String destination = "/notification_topic/friend" + notiMessage.getReceiver().getId();
        messagingTemplate.convertAndSend(destination, notiMessage);
    }

    @KafkaListener(topics = "${kafka.post_topic}",
            groupId = "${spring.kafka.consumer.group-id}",
            properties = {"spring.json.value.default.type=com.social.notificationService.dto.request.message.NotificationPostMessage"}
    )
    @MessageMapping("/noti.sendPostNewsNoti")
    public void addPostNotification(NotificationPostMessage notiMessage) {
        notificationService.store(notiMessage);
        String destination = "/notification_topic/post" + notiMessage.getPostId();
        messagingTemplate.convertAndSend(destination, notiMessage);
    }
}
