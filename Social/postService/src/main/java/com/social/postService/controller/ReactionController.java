package com.social.postService.controller;

import com.social.postService.dto.request.ApiResponse;
import com.social.postService.dto.request.UpdateReactionRequest;
import com.social.postService.dto.response.UserReactionResponse;
import com.social.postService.service.ReactionService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reaction")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReactionController {
    ReactionService reactionService;
    // like là phan ung cap nhat post lien

    @GetMapping("/{postId}")
    public ApiResponse<List<UserReactionResponse>> getReactionInPost(@PathVariable("postId") String postId,
                                                                     @RequestParam("type") String type) {
        return ApiResponse.<List<UserReactionResponse>>builder()
                .result(reactionService.getUserReactionByType(postId, type))
                .build();
    }

    @PostMapping(value = "/update/{post_id}")
    public ApiResponse<?> updateLikePost(@PathVariable("post_id") String id,
                                         @ModelAttribute @Valid UpdateReactionRequest updateReactionRequest) {
        Boolean res = reactionService.storeOrUpdate(id, updateReactionRequest);
        int code = res ? HttpStatus.OK.value() : HttpStatus.INTERNAL_SERVER_ERROR.value();
        return ApiResponse.<Boolean>builder().result(res).code(code).build();
    }

    @DeleteMapping("/delete/{post_id}")
    public ApiResponse<Boolean> deleteLikePost(@PathVariable("post_id") String id) {
        return ApiResponse.<Boolean>builder().result(reactionService.delete(id)).build();
    }
}
