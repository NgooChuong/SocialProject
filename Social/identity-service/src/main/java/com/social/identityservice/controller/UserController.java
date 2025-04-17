package com.social.identityservice.controller;

import com.social.identityservice.dto.request.ApiResponse;
import com.social.identityservice.dto.request.UserCreationRequest;
import com.social.identityservice.dto.request.UserUpdateRequest;
import com.social.identityservice.dto.response.UserResponse;
import com.social.identityservice.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserController {
    UserService userService;
    // xử lý liên quan đến react programing
    @PostMapping(value = "/registration",consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    ApiResponse<UserResponse> createUser(@ModelAttribute @Valid UserCreationRequest request){
        log.info("request:{}", request);
        return ApiResponse.<UserResponse>builder()
                .result(userService.createUser(request))
                .build();
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<List<UserResponse>> getUsers(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("Username: {}", authentication.getName());
        Jwt jwt = (Jwt) authentication.getPrincipal();
        log.info("ROLE: {}", jwt.getClaims().get("role"));
        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.getUsers())
                .build();
    }

    @GetMapping("/{userId}")
    ApiResponse<UserResponse> getUser(@PathVariable("userId") String userId){
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUser(userId))
                .build();
    }

    @DeleteMapping("/{userId}")
    ApiResponse<String> deleteUser(@PathVariable String userId){
        userService.deleteUser(userId);
        return ApiResponse.<String>builder()
                .result("User has been deleted")
                .build();
    }

    @PutMapping(value = "/{userId}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    ApiResponse<String> updateUser(@PathVariable String userId, @ModelAttribute UserUpdateRequest request){
        userService.updateUser(userId, request);
        return ApiResponse.<String>builder()
                .result("Successfully updated user")
                .build();
    }
}
