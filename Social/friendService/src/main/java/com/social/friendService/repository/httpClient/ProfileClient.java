package com.social.friendService.repository.httpClient;

import com.social.friendService.configuration.AuthenticationRequestInterceptor;
import com.social.friendService.dto.request.ApiResponse;
import com.social.friendService.dto.response.InformationResponse;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@LoadBalancerClient(name="PROFILE")
@FeignClient(name="PROFILE", path = "/profile/users", configuration = AuthenticationRequestInterceptor.class)
public interface ProfileClient {
    @GetMapping("/post/{userId}")
    ApiResponse<InformationResponse> getProfileByUserId(@PathVariable String userId);
}
