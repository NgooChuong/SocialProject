package com.social.profileservice.repository.httpClient;


import com.social.profileservice.configuration.AuthenticationRequestInterceptor;
import com.social.profileservice.dto.request.ApiResponse;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@LoadBalancerClient(name="FRIEND")
@FeignClient(name="FRIEND", path = "/friend/api/", configuration = AuthenticationRequestInterceptor.class)
public interface FriendClient {
    @GetMapping("/getAllYourFriends")
    ApiResponse<List<String>> getAllYourFriends();
}

