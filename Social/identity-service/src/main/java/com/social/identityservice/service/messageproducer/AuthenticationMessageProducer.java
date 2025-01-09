package com.social.identityservice.service.messageproducer;

import com.social.identityservice.dto.request.message.GoogleMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Service
public class AuthenticationMessageProducer {
    RabbitTemplate rabbitTemplate;

    public void sendSynchronousGoogleMessage(GoogleMessage googleMessage){
        rabbitTemplate.setReplyTimeout(10000); // Timeout cho việc chờ phản hồi (10s)
        rabbitTemplate.convertSendAndReceive("googleAuthQueue",googleMessage);
    }
}
