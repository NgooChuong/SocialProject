package com.gateway.apigateway.repository;

import com.gateway.apigateway.dto.ApiResponse;
import com.gateway.apigateway.dto.request.IntrospectRequest;
import com.gateway.apigateway.dto.response.IntrospectResponse;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Mono;


public interface IdentityClient {
    @PostExchange(url = "/auth/introspect", contentType = MediaType.APPLICATION_JSON_VALUE)
    Mono<ApiResponse<IntrospectResponse>> introspect(@RequestBody IntrospectRequest request);
}
