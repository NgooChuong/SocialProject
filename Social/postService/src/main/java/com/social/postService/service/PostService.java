package com.social.postService.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.social.postService.dto.request.CreatePostRequest;
import com.social.postService.dto.response.InformationResponse;
import com.social.postService.dto.response.PostResponse;
import com.social.postService.entity.Picture;
import com.social.postService.entity.Post;
import com.social.postService.entity.Tag;
import com.social.postService.entity.User;
import com.social.postService.enums.MediaType;
import com.social.postService.exception.AppException;
import com.social.postService.exception.ErrorCode;
import com.social.postService.mapper.PostMapper;
import com.social.postService.repository.PictureRepository;
import com.social.postService.repository.PostRepository;
import com.social.postService.repository.TagRepository;
import com.social.postService.repository.UserRepository;
import com.social.postService.repository.httpClient.ProfileClient;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PostService {
    PostRepository postRepository;
    TagRepository tagRepository;
    UserRepository userRepository;
    PictureRepository pictureRepository;
    Cloudinary cloudinary;
    ProfileClient profileClient;

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
        return uploadedPics.isEmpty() ? Collections.emptyList() : pictureRepository.saveAll(uploadedPics);
    }

    private User createInstanceUser() { // current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        String userId = jwt.getClaimAsString("userId"); // Lấy userId từ claims
        return userRepository.findById(userId).orElseGet(() -> {
            String username = jwt.getClaimAsString("sub");
            String avatar = profileClient.getProfileByUserId(userId).getResult().getAvatar();
            return userRepository.save(User.builder().id(userId).username(username).avatar(avatar).build());
        });
    }

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

            Post newPost = postRepository.save(Post.builder()
                    .user(createInstanceUser())
                    .title(createPostRequest.getTitle())
                    .content(createPostRequest.getContent())
                    .isPrivate(createPostRequest.getIsPrivate())
                    .mediaType(MediaType.valueOf(createPostRequest.getMediaType()))
                    .pics(pictures)
                    .tags(newTagsList)
                    .build());
            if (newPost.getPics() != null) {
                newPost.getPics().forEach(pic -> pic.setPost(newPost));
            }
            return PostMapper.INSTANCE.toPostResponse(newPost);
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error while processing Post creation", e);
            throw new AppException(ErrorCode.THREAD_ERROR);
        } catch (CompletionException e) {
            log.error("Error while processing Post creation", e);
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }
}
