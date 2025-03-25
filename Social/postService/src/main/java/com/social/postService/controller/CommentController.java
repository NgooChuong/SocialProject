package com.social.postService.controller;

import com.social.postService.dto.request.*;
import com.social.postService.dto.response.CommentResponse;
import com.social.postService.dto.response.PostDetailResponse;
import com.social.postService.dto.response.PostResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentController {

    @PostMapping(value= "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<CommentResponse> createComment(@ModelAttribute @Valid CreateCommentRequest createCommentRequest) {
        return null;
    }
    @PutMapping(value= "/update/{cmt_id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<CommentResponse> updatePost(@PathVariable("cmt_id") String id, @ModelAttribute @Valid UpdateCommentRequest updateCommentRequest) {
        return null;
    }

    @DeleteMapping(value = "/delete/{cmt_id}")
    public void deletePost(@PathVariable("cmt_id") String id) {
    }
}
