package com.social.friendService.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;


@Slf4j
@Configuration
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

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
