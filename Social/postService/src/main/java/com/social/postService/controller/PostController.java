package com.social.postService.controller;

import com.social.postService.dto.request.ApiResponse;
import com.social.postService.dto.request.CreatePostRequest;
import com.social.postService.dto.request.UpdatePostRequest;
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
import org.springframework.http.HttpStatus;
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

    @GetMapping("/MyPosts")
    public ApiResponse<List<PostResponse>> getMyPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.<List<PostResponse>>builder().result(postService.list(page, size)).build();
    }

    @Operation(summary = "Get  my post by ID", description = "Retrieve a post by its ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("/MyPost/{post_id}")
    public ApiResponse<PostResponse> getMyPostDetail(@PathVariable("post_id") String id) {
        return ApiResponse.<PostResponse>builder().result(postService.retrieve(id)).build();
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<PostResponse> createPost(@ModelAttribute @Valid CreatePostRequest createPostRequest) {
        return ApiResponse.<PostResponse>builder().result(postService.store(createPostRequest)).build();
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<PostResponse> updatePost(@PathVariable("id") String id, @ModelAttribute @Valid UpdatePostRequest updatePostRequest) {
        return ApiResponse.<PostResponse>builder().result(postService.update(id, updatePostRequest)).build();
    }

    @DeleteMapping(value = "/delete/{id}")
    public ApiResponse<?> deletePost(@PathVariable("id") String id) {
        postService.delete(id);
        return ApiResponse.builder().code(HttpStatus.NO_CONTENT.value()).build();
    }

}
