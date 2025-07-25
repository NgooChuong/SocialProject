package com.social.profileservice.configuration;

import com.social.profileservice.entity.Interest;
import com.social.profileservice.enums.InterestType;
import com.social.profileservice.repository.InterestRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {

    @Bean
    ApplicationRunner applicationRunner(InterestRepository interestRepository){
        return args -> {
            if (interestRepository.findAll().isEmpty()) {
                log.info("Interest table is empty. Initializing with sample tags...");
                for (InterestType type : InterestType.values()) {
                    Interest interest = Interest.builder()
                            .name(type)
                            .category(type.getCategory())
                            .description(type.getDescription())
                            .isActive(true)
                            .build();
                    interestRepository.save(interest);
                }
            } else {
                log.info("Tag table already contains data. Skipping initialization.");
            }
        };
    }
}
