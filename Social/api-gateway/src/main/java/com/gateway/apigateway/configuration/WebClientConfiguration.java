package com.gateway.apigateway.configuration;

import com.gateway.apigateway.repository.IdentityClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.util.List;

@Configuration
public class WebClientConfiguration {
    //WebClient: Đây là một client không đồng bộ (asynchronous)
    // được sử dụng để gọi các API RESTful
    // hoặc dịch vụ web khác trong các ứng dụng Spring WebFlux.
    @Bean
    WebClient webClient(){
        return WebClient.builder() //Sử dụng builder pattern để tạo ra một WebClient
                .baseUrl("http://localhost:8080/identity")
                .build();
    }


    @Bean // phải khai báo proxy để identityClient chạy được
    IdentityClient identityClient(WebClient webClient){
        //HttpServiceProxyFactory: Là một factory để tạo ra
        // các proxy cho client sử dụng WebClient làm backend.
        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(webClient)).build();
        //httpServiceProxyFactory.createClient(IdentityClient.class):
        // Tạo một instance của IdentityClient dựa trên proxy đã được cấu hình.
        // Điều này cho phép bạn sử dụng IdentityClient
        // như một client HTTP được xây dựng dựa trên WebClient.
        return httpServiceProxyFactory.createClient(IdentityClient.class);
    }

    @Bean
    CorsWebFilter corsWebFilter(){
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("*"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("*"));
        UrlBasedCorsConfigurationSource urlBasedConfigSource = new UrlBasedCorsConfigurationSource();
        urlBasedConfigSource.registerCorsConfiguration("/**", config);
        return new CorsWebFilter(urlBasedConfigSource);
    }
}
