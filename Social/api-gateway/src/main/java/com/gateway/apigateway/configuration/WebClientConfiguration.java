package com.gateway.apigateway.configuration;

import com.gateway.apigateway.repository.IdentityClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.util.List;

@Configuration

public class WebClientConfiguration {

    @Value("${IDENTIFY_URL}")
    private String identify_url;

    @Bean
    @LoadBalanced
    WebClient webClient() {
        return WebClient.builder()
                .baseUrl(identify_url)
                .build();
    }


    @Bean
    IdentityClient identityClient(WebClient webClient) {
        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(webClient)).build();
        return httpServiceProxyFactory.createClient(IdentityClient.class);
    }

    @Bean
    CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("*"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("*"));
        UrlBasedCorsConfigurationSource urlBasedConfigSource = new UrlBasedCorsConfigurationSource();
        urlBasedConfigSource.registerCorsConfiguration("/**", config);
        return new CorsWebFilter(urlBasedConfigSource);
    }
}
