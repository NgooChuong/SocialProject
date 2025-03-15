package com.social.identityservice.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.HeaderBearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final String[] PUBLIC_ENDPOINTS = {"/users/registration",
            "/auth/token", "/auth/introspect","/auth/face/token", "/auth/google/token",

    };
    private final String[] SWAGGER_ENDPOINTS = {"/swagger-ui/**",
            "/v3/api-docs/**", "/swagger-resources/**",
            "/webjars/**"};
    @Autowired
    private CustomJwtDecoder customJwtDecoder;

    @Value("${jwt.signerKey}")
    private String signerKey;

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
            httpSecurity.authorizeHttpRequests(request ->
                    request.requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINTS).permitAll()
                            .requestMatchers(SWAGGER_ENDPOINTS).permitAll()
                            .anyRequest().authenticated()
            );
            httpSecurity.oauth2ResourceServer(oauth2 ->
                    // tự động gắn Bear vào token
                    oauth2.bearerTokenResolver(new HeaderBearerTokenResolver("Authorization")).
                            jwt(jwtConfigurer ->
                            jwtConfigurer.decoder(customJwtDecoder)// parse ve kieu JWT
                                    .jwtAuthenticationConverter(jwtAuthenticationConverter())// gắn thêm prefix role
                    ).authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                    //JwtAuthenticationEntryPoint: để trả về reponse theo chuẩn RESTful khi thuc hien xac thuc
            );
            httpSecurity.csrf(AbstractHttpConfigurer::disable);
            return httpSecurity.build();
        }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

        // Xác định tiền tố ROLE_
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        // Xác định tên của claim chứa role
        grantedAuthoritiesConverter.setAuthoritiesClaimName("role");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return converter;
    }
    // mã hóa và xác thực
//    @Bean
//    JwtDecoder jwtDecoder(){
//        SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HS512");
//        return NimbusJwtDecoder
//                .withSecretKey(secretKeySpec)
//                .macAlgorithm(MacAlgorithm.HS512)
//                .build();
//    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(10);
    }
}
