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

    @Operation(summary = "Get all my posts")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "1006", description = "Unauthenticated"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "1000", description = "Success")
    })
    @GetMapping("/MyPosts")
    public ApiResponse<List<PostResponse>> getMyPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.<List<PostResponse>>builder().result(postService.list(page, size)).build();
    }

    @Operation(summary = "Get my post by ID", description = "Retrieve a post by its ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "1012", description = "POST_NOT_EXISTED"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "1000", description = "Success")
    })
    @GetMapping("/MyPost/{post_id}")
    public ApiResponse<PostResponse> getMyPostDetail(@PathVariable("post_id") String id) {
        return ApiResponse.<PostResponse>builder().result(postService.retrieve(id)).build();
    }

    @Operation(summary = "Create post")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "1011", description = "Server error - Thread error"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "1000", description = "Success"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "9999", description = "Server uncategorized error"),

    })
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<PostResponse> createPost(@ModelAttribute @Valid CreatePostRequest createPostRequest) {
        return ApiResponse.<PostResponse>builder().result(postService.store(createPostRequest)).build();
    }
    @Operation(summary = "Update post")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "1012", description = "post not existed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "1000", description = "Success"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "1017", description = "Update is error"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "9999", description = "Server uncategorized error"),

    })
    @PutMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<PostResponse> updatePost(@PathVariable("id") String id, @ModelAttribute @Valid UpdatePostRequest updatePostRequest) {
        return ApiResponse.<PostResponse>builder().result(postService.update(id, updatePostRequest)).build();
    }

    @Operation(summary = "Delete post")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "1012", description = "Post not existed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "1000", description = "Success"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "1018", description = "Delete is error"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "1007", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "9999", description = "Server uncategorized error"),


    })
    @DeleteMapping(value = "/delete/{id}")
    public ApiResponse<?> deletePost(@PathVariable("id") String id) {
        postService.delete(id);
        return ApiResponse.builder().code(HttpStatus.NO_CONTENT.value()).build();
    }

}
