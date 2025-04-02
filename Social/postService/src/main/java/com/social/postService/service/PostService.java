package com.social.postService.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.social.postService.dto.request.CreatePostRequest;
import com.social.postService.dto.request.UpdatePostRequest;
import com.social.postService.dto.response.InformationResponse;
import com.social.postService.dto.response.PostResponse;
import com.social.postService.entity.*;
import com.social.postService.enums.MediaType;
import com.social.postService.exception.AppException;
import com.social.postService.exception.ErrorCode;
import com.social.postService.mapper.PostMapper;
import com.social.postService.repository.*;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;


@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PostService {
    PostRepository postRepository;
    TagRepository tagRepository;
    PictureRepository pictureRepository;
    Cloudinary cloudinary;
    CommonService commonService;
    UserPostInteractionRepository userPostInteractionRepository;
    CommentRepository commentRepository;


    private void storeTagsNonAvailable(List<String> tags) {
        if (tags.isEmpty()) return;
        List<Tag> newTags = tags.stream().map(
                tag -> Tag.builder()
                        .name(tag)
                        .count(0)
                        .isActive(true)
                        .slug(tag.toLowerCase())
                        .build()
        ).toList();
        List<Tag> tagsList = tagRepository.findAll();
        tagRepository.saveAll(newTags.stream()
                .filter(tag -> tagsList.stream().noneMatch(t -> t.getName().equals(tag.getName()))).toList());
    }

    private List<Picture> storePicsToCloudinary(List<MultipartFile> Pics) {
        if (Pics.isEmpty()) return null;
        List<CompletableFuture<Picture>> futurePics = Pics.stream().map(pic ->
                CompletableFuture.supplyAsync(() -> {
                    try {
                        Map res = this.cloudinary.uploader().upload(pic.getBytes(),
                                ObjectUtils.asMap("resource_type", "auto"));
                        return Picture.builder().url(res.get("secure_url").toString()).build();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })).toList();
        CompletableFuture<List<Picture>> allFutures = CompletableFuture.allOf(futurePics.toArray(new CompletableFuture[0]))
                .thenApply(v -> futurePics.stream()
                        .map(future -> {
                            try {
                                return future.join(); // Lấy kết quả
                            } catch (Exception e) {
                                log.error("Error in picture upload", e);
                                return null; // Bỏ qua file lỗi
                            }
                        })
                        .filter(Objects::nonNull)
                        .toList());
        List<Picture> uploadedPics = allFutures.join();
        return uploadedPics.isEmpty() ? Collections.emptyList() : uploadedPics;
    }

//    private User createInstanceUser() { // current user
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
//            throw new AppException(ErrorCode.UNAUTHENTICATED);
//        }
//        String userId = jwt.getClaimAsString("userId"); // Lấy userId từ claims
//        return userRepository.findById(userId).orElseGet(() -> {
//            String username = jwt.getClaimAsString("sub");
//            String avatar = profileClient.getProfileByUserId(userId).getResult().getAvatar();
//            return userRepository.save(User.builder().id(userId).username(username).avatar(avatar).build());
//        });
//    }

    public PostResponse store(CreatePostRequest createPostRequest) {
        List<String> requestedTags = Optional.ofNullable(createPostRequest.getTags())
                .orElse(Collections.emptyList());
        storeTagsNonAvailable(requestedTags);

        CompletableFuture<List<Tag>> tagsFuture = CompletableFuture.supplyAsync(() ->
                tagRepository.findByNameIn(requestedTags));
        CompletableFuture<List<Picture>> picsFuture = CompletableFuture.supplyAsync(() ->
                storePicsToCloudinary(createPostRequest.getPictures()));
        CompletableFuture.allOf(tagsFuture, picsFuture).join();

        try {
            List<Tag> newTagsList = tagsFuture.get();
            List<Picture> pictures = picsFuture.get();

            Post newPost = Post.builder()
                    .user(commonService.createInstanceUser())
                    .title(createPostRequest.getTitle())
                    .content(createPostRequest.getContent())
                    .isPrivate(createPostRequest.getIsPrivate())
                    .mediaType(MediaType.valueOf(createPostRequest.getMediaType()))
                    .pics(pictures)
                    .tags(newTagsList)
                    .build();
            if (newPost.getPics() != null) {
                log.info("abc");
                pictures.forEach(pic -> pic.setPost(newPost));
                pictureRepository.saveAll(pictures);
            }
            PostResponse p = PostMapper.INSTANCE.toPostResponse(postRepository.save(newPost));
            p.setCountLikes(0);
            p.setCountComments(0);
            return p;
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error while processing Post creation", e);
            throw new AppException(ErrorCode.THREAD_ERROR);
        } catch (CompletionException e) {
            log.error("Error while processing Post creation", e);
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    // Trích xuất public_id từ URL
    private String extractPublicIdFromUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            throw new IllegalArgumentException("Image URL cannot be null or empty");
        }

        // Ví dụ URL: https://res.cloudinary.com/your_cloud_name/image/upload/v1234567890/folder/image123.jpg
        String[] parts = imageUrl.split("/");

        // Tìm phần sau "upload" hoặc "v{version}"
        int uploadIndex = -1;
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].equals("upload") || parts[i].startsWith("v")) {
                uploadIndex = i;
                break;
            }
        }

        if (uploadIndex == -1 || uploadIndex + 1 >= parts.length) {
            throw new IllegalArgumentException("Invalid Cloudinary URL: " + imageUrl);
        }

        // Lấy phần sau "upload" hoặc "v{version}" và bỏ đuôi file
        String publicIdWithExt = String.join("/", Arrays.copyOfRange(parts, uploadIndex + 1, parts.length));
        return publicIdWithExt.substring(0, publicIdWithExt.lastIndexOf(".")); // Bỏ phần mở rộng (.jpg, .png, v.v.)
    }

    private void deletePicInCloud(Picture pictures) {
        try {
            String publicId = extractPublicIdFromUrl(pictures.getUrl());
            Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            log.info("Deleted image with public_id: {}. Result: {}", publicId, result);
        } catch (Exception e) {
            log.error("Error deleting image with URL: {}", pictures.getUrl(), e);
            throw new AppException(ErrorCode.CLOUDINARY_DELETE_FAILED);
        }
    }

    private void deletePicsInCloud(List<Picture> pictures) {
        pictures.forEach(this::deletePicInCloud);
    }

    private Integer getNumberOfComments (List<UserPostInteraction> us) {
        return us.stream()
                .flatMap(u -> {
                    List<Comment> comments = commentRepository.findByUserPostInteraction(u).get();
                    return comments.stream();
                })
                .toList().size();
    }



    public List<PostResponse> list ( int page, int size) {
        User user = commonService.createInstanceUser();
        Pageable p = PageRequest.of(page, size, Sort.by("createdAt").descending());
        List<Post> posts = postRepository.findByUserId(user.getId(), p).stream().toList();
        List<PostResponse> postResponses = posts.stream().map(PostMapper.INSTANCE::toPostResponse).toList();
        for (int i = 0; i< postResponses.size(); i++) {
            List<UserPostInteraction> us = userPostInteractionRepository.findByPost(posts.get(i)).orElse(Collections.emptyList())
                    .stream().filter(u -> Objects.nonNull(u.getReaction())).toList();
            postResponses.get(i).setCountLikes(us.size());
            postResponses.get(i).setCountComments(getNumberOfComments(us));
        }
        return postResponses;
    }

    public PostResponse retrieve(String postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXISTED));
        PostResponse p =  PostMapper.INSTANCE.toPostResponse(post);
        List<UserPostInteraction> us = userPostInteractionRepository.findByPost(post).orElse(Collections.emptyList())
                .stream().filter(u -> Objects.nonNull(u.getReaction())).toList();
        p.setCountLikes(us.size());
        p.setCountComments(getNumberOfComments(us));
        return p;
    }

    public PostResponse update(String postId, UpdatePostRequest updatePostRequest) {
        // Tìm bài viết hoặc ném lỗi nếu không tồn tại
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXISTED));
        MediaType mediaType = Objects.isNull(updatePostRequest.getMediaType()) ?
                post.getMediaType() : MediaType.valueOf(updatePostRequest.getMediaType());
        // Cập nhật các trường từ UpdatePostRequest
        Optional.ofNullable(updatePostRequest.getTitle()).ifPresent(post::setTitle);
        Optional.ofNullable(updatePostRequest.getContent()).ifPresent(post::setContent);
        Optional.ofNullable(updatePostRequest.getIsPrivate()).ifPresent(post::setIsPrivate);
        Optional.of(mediaType).ifPresent(post::setMediaType);
        List<String> requestedTags = Optional.ofNullable(updatePostRequest.getTags())
                .orElse(Collections.emptyList());
        storeTagsNonAvailable(requestedTags);
        List<Tag> tags = tagRepository.findByNameIn(requestedTags);
        Optional.of(tags.isEmpty() ? post.getTags() : tags).ifPresent(post::setTags);
        // update  hinh
        try {
            List<Picture> pictures = storePicsToCloudinary(updatePostRequest.getPictures());
            pictureRepository.deleteAll(post.getPics());
            post.setPics(pictures);
            if (post.getPics() != null) {
                post.getPics().forEach(pic -> pic.setPost(post));
            }
        } catch (Exception e) {
            log.error("Error while updating picture", e);
            throw new AppException(ErrorCode.UPDATE_ERROR);
        }
        // Lưu và trả về kết quả
        PostResponse p = PostMapper.INSTANCE.toPostResponse(postRepository.save(post));
        List<UserPostInteraction> us = userPostInteractionRepository.findByPost(post).orElseGet(() -> {
            p.setCountComments(0);
            p.setCountLikes(0);
            return Collections.emptyList();
        }).stream().filter(u -> Objects.nonNull(u.getReaction())).toList();
        if (us.isEmpty()) return p;

        p.setCountLikes(us.size());
        p.setCountComments(this.getNumberOfComments(us));
        return p;
    }

    public void delete(String postId) {
        User user = commonService.createInstanceUser();
        Post p = postRepository.findById(postId).orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXISTED));
        if (!p.getUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        try {
            postRepository.deleteById(postId);
        } catch (Exception e) {
            throw new AppException(ErrorCode.DELETE_ERROR);
        }
    }
}
