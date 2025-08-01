package com.social.postService.repository.httpClient;

import com.social.postService.configuration.AuthenticationRequestInterceptor;
import com.social.postService.dto.request.ApiResponse;
import com.social.postService.dto.response.InformationResponse;
import com.social.postService.dto.response.UserResponse;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@LoadBalancerClient(name="FRIEND")
@FeignClient(name="FRIEND", path = "/friend/api", configuration = AuthenticationRequestInterceptor.class)
public interface FriendClient {
    @GetMapping("/")
    ApiResponse<Page<UserResponse>> getFriends(@RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "10") int size,
                                               @RequestParam(defaultValue = "ACCEPTED") String type);
    @GetMapping("/getAllYourFriends")
    ApiResponse<List<String>> getAllYourFriends();
}
