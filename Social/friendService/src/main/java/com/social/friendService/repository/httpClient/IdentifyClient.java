package com.social.friendService.repository.httpClient;

import com.social.friendService.configuration.AuthenticationRequestInterceptor;
import com.social.friendService.dto.request.ApiResponse;
import com.social.friendService.dto.response.IdentifyResponse;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@LoadBalancerClient(name="IDENTIFY")
@FeignClient(name="IDENTIFY", path = "/identity/users", configuration = AuthenticationRequestInterceptor.class)
public interface IdentifyClient {
    @GetMapping("/{userId}")
    ApiResponse<IdentifyResponse> getUser(@PathVariable("userId") String userId);
}
