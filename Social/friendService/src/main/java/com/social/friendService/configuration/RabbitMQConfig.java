package com.social.friendService.configuration;


import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {

//    @Value("${rabbit.queue.google}")
//    private String googleAuthQueue;
//
//    @Bean
//    public Queue GoogleAuthQueue(){
//        return new Queue(googleAuthQueue);
//    }
//
//    @Bean
//    public MessageConverter messageConverter(){
//        return new Jackson2JsonMessageConverter();
//    }
//
//    @Bean
//    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory){
//        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
//        rabbitTemplate.setMessageConverter(messageConverter());
//        return rabbitTemplate;
//    }

}
