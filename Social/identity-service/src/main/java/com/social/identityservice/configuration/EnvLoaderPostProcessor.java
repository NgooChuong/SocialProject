package com.social.identityservice.configuration;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class EnvLoaderPostProcessor implements EnvironmentPostProcessor {
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // Bỏ qua nếu đang chạy trong Docker
        if (System.getenv("DOCKER_ENV") != null) {
            return;
        }

        // Đường dẫn thư mục hiện tại
        Path currentDir = Paths.get("").toAbsolutePath();
        String profile = System.getenv("SPRING_PROFILES_ACTIVE");
        if (profile == null || profile.isEmpty()) {
            profile = environment.getProperty("spring.profiles.active", "development");
        }

        String envFile = ".env";
        Path envDir = currentDir; // mặc định

        if ("production".equalsIgnoreCase(profile)) {
            envFile = ".env_production";
            envDir = currentDir.getParent(); // lên 1 cấp
        }
        System.out.println(envFile);
        Dotenv dotenv = Dotenv.configure()
                .filename(envFile)
                .directory(envDir.toString())
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();

        Map<String, Object> envMap = new HashMap<>();
        dotenv.entries().forEach(entry -> envMap.put(entry.getKey(), entry.getValue()));
        environment.getPropertySources().addFirst(new MapPropertySource("dotenv", envMap));
    }
}
