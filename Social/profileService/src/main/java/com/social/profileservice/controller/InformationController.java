package com.social.profileservice.controller;

import com.social.profileservice.dto.request.ApiResponse;
import com.social.profileservice.dto.request.InformationCreateRequest;
import com.social.profileservice.dto.request.InformationUpdateRequest;
import com.social.profileservice.dto.response.InformationResponse;
import com.social.profileservice.service.InformationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class InformationController {
    InformationService informationService;
    @GetMapping("/{profileId}")
    ApiResponse<InformationResponse> getProfile(@PathVariable String profileId) {
        return ApiResponse.<InformationResponse>builder()
                .result(informationService.getProfileByUserId(profileId))
                .build();
    }

    @GetMapping("/post/{userId}")
    ApiResponse<InformationResponse> getProfileByUserId(@PathVariable String userId) {
        return ApiResponse.<InformationResponse>builder()
                .result(informationService.getProfileByUserId(userId))
                .build();
    }

    @PostMapping
    ApiResponse<InformationResponse> createProfile(@ModelAttribute InformationCreateRequest request) {
        return ApiResponse.<InformationResponse>builder()
                .result(informationService.createProfile(request))
                .build();
    }
    @DeleteMapping("/{profileId}")
    ApiResponse<String> deleteUser(@PathVariable String profileId){
        informationService.deleteUser(profileId);
        return ApiResponse.<String>builder()
                .result("User has been deleted")
                .build();
    }

    @PutMapping(value = "/{profileId}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    ApiResponse<InformationResponse> updateUser(@PathVariable String profileId, @ModelAttribute InformationUpdateRequest request){
        return ApiResponse.<InformationResponse>builder()
                .result(informationService.updateUser(profileId, request))
                .build();
    }
}
