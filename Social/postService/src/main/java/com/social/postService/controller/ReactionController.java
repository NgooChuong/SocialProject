package com.social.postService.controller;

import com.social.postService.dto.request.ApiResponse;
import com.social.postService.dto.request.UpdatePostRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/like")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReactionController {
    // like là phan ung cap nhat post lien
    @PutMapping(value= "/update/{post_id}")
    public ApiResponse<?> updateLikePost(@PathVariable("post_id") String id, @ModelAttribute @Valid UpdatePostRequest updatePostRequest) {
        return null;
    }
}
