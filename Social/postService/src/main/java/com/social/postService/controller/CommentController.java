package com.social.postService.controller;

import com.social.postService.dto.request.*;
import com.social.postService.dto.response.CommentResponse;
import com.social.postService.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentController {
    CommentService commentService;
    SimpMessagingTemplate messagingTemplate;
    @Operation(summary = "get comment in post")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "1012", description = "Post not existed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "1000", description = "Success"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "9999", description = "Server uncategorized error"),
    })
    @GetMapping("/{postId}")
    public ApiResponse<List<CommentResponse>> getCommentInPost
            (@PathVariable String postId,
            @RequestParam(defaultValue = "0") int page,
             @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.<List<CommentResponse>>builder()
                .result(commentService.list(postId, page, size))
                .build();
    }

//        @PostMapping(value = "/create")
    @Operation(summary = "create and send realtime comment in post")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "1012", description = "Post not existed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "1000", description = "Success"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "1019", description = "Create comment failed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "9999", description = "Server uncategorized error"),
    })
    @MessageMapping("/chat.sendComment")
    public void createComment(@Payload CreateCommentRequest createCommentRequest) {
        CommentResponse message =  commentService.store(createCommentRequest);
        String destination = "/topic/post" + createCommentRequest.getPostId();
        messagingTemplate.convertAndSend(destination, message);
    }
    @Operation(summary = "update comment in post")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "1020", description = "Comment not existed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "1000", description = "Success"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "1021", description = "Update comment failed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "1007", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "9999", description = "Server uncategorized error"),
    })
    @PutMapping(value = "/update/{cmt_id}")
    public ApiResponse<CommentResponse> updateComment(@PathVariable("cmt_id") String id, @RequestBody String newComment) {
        return ApiResponse.<CommentResponse>builder()
                .result(commentService.update(id, newComment))
                .build();
    }
    @Operation(summary = "delete comment in post")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "1020", description = "Comment not existed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "1000", description = "Success"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "1022", description = "delete comment failed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "1007", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "9999", description = "Server uncategorized error"),
    })
    @DeleteMapping(value = "/delete/{cmt_id}")
    public ApiResponse<?> deletePost(@PathVariable("cmt_id") String id) {
        commentService.delete(id);
        return ApiResponse.<CommentResponse>builder()
                .code(HttpStatus.NO_CONTENT.value())
                .build();
    }
}
