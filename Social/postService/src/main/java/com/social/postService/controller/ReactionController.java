package com.social.postService.controller;

import com.social.postService.dto.request.ApiResponse;
import com.social.postService.dto.request.UpdateReactionRequest;
import com.social.postService.dto.response.UserReactionResponse;
import com.social.postService.service.ReactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "get reaction in post by type")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "1012", description = "post not existed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "1013", description = "Invalid reaction type"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "1000", description = "Success"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "1015", description = "Interaction not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "9999", description = "Server uncategorized error"),
    })
    @GetMapping("/{postId}")
    public ApiResponse<List<UserReactionResponse>> getReactionInPost(
            @PathVariable("postId") String postId,
            @RequestParam(value = "type", defaultValue = "ALL") String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.<List<UserReactionResponse>>builder()
                .result(reactionService.getUserReactionByType(postId, type, page, size))
                .build();
    }
    @Operation(summary = "create reaction in post", description = "True when reaction is reacted but False isn't")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "1000", description = "Success"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "9999", description = "Server uncategorized error"),
    })
    @PostMapping(value = "/create/{post_id}")
    public ApiResponse<?> updateLikePost(@PathVariable("post_id") String id,
                                         @ModelAttribute @Valid UpdateReactionRequest updateReactionRequest) {
        Boolean res = reactionService.storeOrUpdate(id, updateReactionRequest);
        int code = res ? HttpStatus.OK.value() : HttpStatus.INTERNAL_SERVER_ERROR.value();
        return ApiResponse.<Boolean>builder().result(res).code(code).build();
    }
    @Operation(summary = "delete reaction in post", description = "True when reaction is deleted but False isn't")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "1012", description = "Post not existed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "1015", description = "Interaction not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "1000", description = "Success"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "9999", description = "Server uncategorized error"),
    })
    @DeleteMapping("/delete/{post_id}")
    public ApiResponse<Boolean> deleteLikePost(@PathVariable("post_id") String id) {
        return ApiResponse.<Boolean>builder().result(reactionService.delete(id)).build();
    }
}
