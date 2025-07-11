package com.social.postService.configuration;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.social.postService.entity.Tag;
import com.social.postService.repository.TagRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {
    private static final Object[][] TAG_DATA = {
            {"Technology", "Posts about tech and innovation", "technology"},
            {"Travel", "Travel experiences and tips", "travel"},
            {"Food", "Recipes and food-related content", "food"}
    };
    @Bean
    ApplicationRunner applicationRunner(TagRepository tagRepository){
        return args -> {
            if (tagRepository.findAll().isEmpty()) {
                log.info("Tag table is empty. Initializing with sample tags...");
                List<Tag> tags = Arrays.stream(TAG_DATA)
                        .map(data -> Tag.builder()
                                .name((String) data[0])
                                .description((String) data[1])
                                .slug((String) data[2])
                                .count(0)
                                .isActive(true)
                                .build())
                        .toList();
                tagRepository.saveAll(tags);
                log.info("Initialized {} tags successfully.", tags.size());
            } else {
                log.info("Tag table already contains data. Skipping initialization.");
            }
        };
    }
}
