package com.gateway.apigateway.service;

import com.gateway.apigateway.dto.ApiResponse;
import com.gateway.apigateway.dto.request.IntrospectRequest;
import com.gateway.apigateway.dto.response.IntrospectResponse;
import com.gateway.apigateway.repository.IdentityClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IdentityService {
    IdentityClient identityClient;

    public Mono<ApiResponse<IntrospectResponse>> introspect(IntrospectRequest introspectRequest) {
        return identityClient.introspect(IntrospectRequest.builder()
                .token(introspectRequest.getToken())
                .refreshToken(introspectRequest.getRefreshToken())
                .build());
    }
}
