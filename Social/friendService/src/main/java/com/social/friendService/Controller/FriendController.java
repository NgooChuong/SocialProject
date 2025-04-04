package com.social.friendService.Controller;

import com.social.friendService.dto.request.ApiResponse;
import com.social.friendService.dto.request.FriendStatusRequest;
import com.social.friendService.dto.response.UserResponse;
import com.social.friendService.service.FriendService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FriendController {
    FriendService friendService;

    @PostMapping("/add/{friend_id}")
    public ApiResponse<Boolean> addFriend(@PathVariable String friend_id) {
        friendService.sendAddFriendRequest(friend_id);
        return ApiResponse.<Boolean>builder().result(true).build();
    }

    @PostMapping(value = "/accept/{friend_id}")
    public ApiResponse<Boolean> acceptFriend(@PathVariable String friend_id, @ModelAttribute FriendStatusRequest friendStatusRequest) {
        friendService.acceptOrReject(friend_id, friendStatusRequest);
        return ApiResponse.<Boolean>builder().result(true).build();
    }

    @PostMapping("/reject/{friend_id}")
    public ApiResponse<Boolean> rejectFriend(@PathVariable String friend_id, @ModelAttribute FriendStatusRequest friendStatusRequest) {
        friendService.acceptOrReject(friend_id, friendStatusRequest);
        return ApiResponse.<Boolean>builder().result(true).build();
    }

    @PostMapping("/unfriend/{friend_id}")
    public ApiResponse<Boolean> removeFriend(@PathVariable String friend_id) {
        friendService.removeFriend(friend_id);
        return ApiResponse.<Boolean>builder().result(true).build();
    }

    @GetMapping("/")
    public ApiResponse<Page<UserResponse>> getFriends(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ACCEPTED") String type
    ) {
        return ApiResponse.<Page<UserResponse>>builder().result(friendService.getFriends(page,size,type)).build();
    }
}
