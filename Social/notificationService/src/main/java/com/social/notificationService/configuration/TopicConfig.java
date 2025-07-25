package com.social.notificationService.configuration;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "kafka")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)

public class TopicConfig {
    String friend_topic_add;
    String friend_topic_acp;
    String post_topic;
}
