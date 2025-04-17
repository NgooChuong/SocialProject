package com.social.friendService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

@SpringBootApplication
@EnableFeignClients
@EnableWebSocketMessageBroker
@EnableKafka
@EnableAsync
public class FriendServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FriendServiceApplication.class, args);
	}

}
