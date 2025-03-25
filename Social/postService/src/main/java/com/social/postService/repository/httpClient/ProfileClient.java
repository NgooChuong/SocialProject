package com.social.postService.repository.httpClient;

import com.social.postService.configuration.AuthenticationRequestInterceptor;
import com.social.postService.dto.request.ApiResponse;
import com.social.postService.dto.response.InformationResponse;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@LoadBalancerClient(name="PROFILE")
@FeignClient(name="PROFILE", path = "/profile/users", configuration = AuthenticationRequestInterceptor.class)
public interface ProfileClient {
    @GetMapping("/post/{userId}")
    ApiResponse<InformationResponse> getProfileByUserId(@PathVariable String userId);
}
