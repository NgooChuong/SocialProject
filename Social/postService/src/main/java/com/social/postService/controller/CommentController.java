package com.social.postService.controller;

import com.social.postService.dto.request.*;
import com.social.postService.dto.response.CommentResponse;
import com.social.postService.service.CommentService;
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
    @MessageMapping("/chat.sendComment")
    public void createComment(@Payload CreateCommentRequest createCommentRequest) {
        CommentResponse message =  commentService.store(createCommentRequest);
        String destination = "/topic/post" + createCommentRequest.getPostId();
        messagingTemplate.convertAndSend(destination, message);
    }

    @PutMapping(value = "/update/{cmt_id}")
    public ApiResponse<CommentResponse> updateComment(@PathVariable("cmt_id") String id, @RequestBody String newComment) {
        return ApiResponse.<CommentResponse>builder()
                .result(commentService.update(id, newComment))
                .build();
    }

    @DeleteMapping(value = "/delete/{cmt_id}")
    public ApiResponse<?> deletePost(@PathVariable("cmt_id") String id) {
        commentService.delete(id);
        return ApiResponse.<CommentResponse>builder()
                .code(HttpStatus.NO_CONTENT.value())
                .build();
    }
}
