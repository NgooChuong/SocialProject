package com.social.postService.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.socket.config.annotation.*;

import java.util.Collections;
import java.util.List;

@Slf4j
@Configuration
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private CustomJwtDecoder customJwtDecoder;
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        log.info("Registering WebSocket endpoint: /post/ws");
        registry.addEndpoint("/ws").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        log.info("Registering WebSocket endpoint: /post/ws 2");

        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/topic");
    }

//    @Override
//    public void configureClientInboundChannel(ChannelRegistration registration) {
//        registration.interceptors(new ChannelInterceptor() {
//            @Override
//            public Message<?> preSend(Message<?> message, MessageChannel channel) {
//                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
//                List<String> authHeader = accessor.getNativeHeader("Authorization");
//
//                if (authHeader != null && !authHeader.isEmpty()) {
//                    String token = authHeader.getFirst().replace("Bearer ", "");
//                    try {
//                        Jwt jwt = customJwtDecoder.decode(token);
//                        // Thêm JWT hoặc userId vào header của message
//                        accessor.setUser(new UsernamePasswordAuthenticationToken(jwt, null, Collections.emptyList()));
//                        log.info("Set authentication for token: {}", token);
//                    } catch (JwtException e) {
//                        log.error("Invalid JWT token: {}", e.getMessage());
//                    }
//                } else {
//                    log.warn("No Authorization header found");
//                }
//                return message;
//            }
//        });
//    }
}
