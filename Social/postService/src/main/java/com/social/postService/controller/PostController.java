package com.social.postService.controller;

import com.social.postService.dto.request.ApiResponse;
import com.social.postService.dto.request.CreatePostRequest;
import com.social.postService.dto.request.UpdatePostRequest;
import com.social.postService.dto.response.PostDetailResponse;
import com.social.postService.dto.response.PostResponse;
import com.social.postService.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/news")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Post API", description = "Operations related to posts")
public class PostController {

    PostService postService;

    @GetMapping("/{user_id}")
    public ApiResponse<List<PostResponse>> getMyPosts(@PathVariable("user_id") String id) {
        return null;
    }
    @Operation(summary = "Get  my post by ID", description = "Retrieve a post by its ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("/MyPosts/{post_id}")
    public ApiResponse<PostDetailResponse> getMyPostDetail(@PathVariable("post_id") String id) {
        return null;
    }

    @PostMapping(value= "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<PostResponse> createPost(@ModelAttribute @Valid CreatePostRequest createPostRequest) {
        return ApiResponse.<PostResponse>builder().result(postService.store(createPostRequest)).build();
    }
    @PutMapping(value= "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<PostResponse> updatePost(@PathVariable("id") String id, @ModelAttribute @Valid UpdatePostRequest updatePostRequest) {
        return null;
    }

    @DeleteMapping(value = "/delete/{id}")
    public void deletePost(@PathVariable("id") String id) {
    }

}
