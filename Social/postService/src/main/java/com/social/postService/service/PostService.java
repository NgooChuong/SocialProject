package com.social.postService.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.social.postService.dto.request.ApiResponse;
import com.social.postService.dto.request.CreatePostRequest;
import com.social.postService.dto.request.UpdatePostRequest;
import com.social.postService.dto.request.message.NotificationPostMessage;
import com.social.postService.dto.request.message.UserNotiMessage;
import com.social.postService.dto.response.InformationResponse;
import com.social.postService.dto.response.PostResponse;
import com.social.postService.dto.response.ReactionResponse;
import com.social.postService.dto.response.UserResponse;
import com.social.postService.entity.*;
import com.social.postService.enums.MediaType;
import com.social.postService.exception.AppException;
import com.social.postService.exception.ErrorCode;
import com.social.postService.mapper.PostMapper;
import com.social.postService.mapper.UserMapper;
import com.social.postService.repository.*;
import com.social.postService.repository.httpClient.FriendClient;
import com.social.postService.service.messageproducer.KafkaProducerService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Service
@Transactional
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final PictureRepository pictureRepository;
    private final Cloudinary cloudinary;
    private final CommonService commonService;
    private final UserPostInteractionRepository userPostInteractionRepository;
    private final CommentRepository commentRepository;
    private final FriendClient friendClient;
    private final KafkaProducerService kafkaProducerService;
    private final RedisService redisService;
    private static final ExecutorService uploadExecutor = Executors.newFixedThreadPool(4);

    @Value("${kafka.topic}")
    private String topic;

    public PostService(PostRepository postRepository, TagRepository tagRepository, PictureRepository pictureRepository, Cloudinary cloudinary, CommonService commonService, UserPostInteractionRepository userPostInteractionRepository, CommentRepository commentRepository, FriendClient friendClient, KafkaProducerService kafkaProducerService, RedisService redisService) {
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
        this.pictureRepository = pictureRepository;
        this.cloudinary = cloudinary;
        this.commonService = commonService;
        this.userPostInteractionRepository = userPostInteractionRepository;
        this.commentRepository = commentRepository;
        this.friendClient = friendClient;
        this.kafkaProducerService = kafkaProducerService;
        this.redisService = redisService;
    }

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
        if (Pics == null || Pics.isEmpty()) return new ArrayList<>();
        List<CompletableFuture<Picture>> futurePics = Pics.stream().map(pic ->
                CompletableFuture.supplyAsync(() -> {
                    try {
                        long start = System.currentTimeMillis();
                        Map res = this.cloudinary.uploader().upload(pic.getBytes(),
                                ObjectUtils.asMap("resource_type", "auto"));
                        log.info("Uploaded file: {} in {}ms", pic.getOriginalFilename(), System.currentTimeMillis() - start);
                        return Picture.builder().url(res.get("secure_url").toString()).build();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                },uploadExecutor)).toList();
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
    private UserNotiMessage createUserMessage(User user) {
        return UserNotiMessage.builder()
                .id(user.getId())
                .username(user.getUsername())
                .avatar(user.getAvatar()).build();
    }

    private NotificationPostMessage createNotificationFriendMessage(User user, User friend, String postId) {
        UserNotiMessage sender = createUserMessage(user);
        UserNotiMessage receiver = createUserMessage(friend);
        return NotificationPostMessage.builder()
                .sender(sender)
                .receiver(receiver)
                .postId(postId)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();
    }

    private List<UserResponse> fetchAllFriendsAsync(String type) {
        int size = 100;
        ApiResponse<Page<UserResponse>> firstPageResponse = friendClient.getFriends(0, size, type);
        int totalPages = firstPageResponse.getResult().getTotalPages();

        // Tạo danh sách CompletableFuture cho từng page
        List<CompletableFuture<List<UserResponse>>> futures = new ArrayList<>();
        futures.add(CompletableFuture.completedFuture(firstPageResponse.getResult().getContent())); // Page 0 đã có

        // Gọi các page còn lại bất đồng bộ
        IntStream.range(1, totalPages)
                .forEach(page -> {
                    CompletableFuture<List<UserResponse>> future = CompletableFuture.supplyAsync(() -> {
                        try {
                            ApiResponse<Page<UserResponse>> response = friendClient.getFriends(page, size, type);
                            return response.getResult().getContent();
                        } catch (Exception e) {
                            log.error("Failed to fetch page {}: {}", page, e.getMessage());
                            throw new RuntimeException("Error fetching page " + page, e);
                        }
                    });
                    futures.add(future);
                });

        // Chờ tất cả future hoàn tất và gộp kết quả
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .flatMap(List::stream)
                        .toList())
                .join();
    }



    @Async
    public void sendMessagesPostToFriendsAsync(String postId) {
        try {
            sendMessagesPostToFriendsSync(postId);
        } catch (Exception e) {
            log.error("Async FriendService lỗi. Đẩy postId vào Redis để retry: {}", postId);
            String redisKey = "retry:post:" + postId;
            redisService.save(redisKey, postId, 300); // Tránh overwrite
        }
    }

    // Hàm sync dùng trong scheduler hoặc async wrapper
    public void sendMessagesPostToFriendsSync(String postId) {
        List<UserResponse> listFriends = fetchAllFriendsAsync("ACCEPTED");
        log.info("Danh sách bạn bè: {}", listFriends);
        User sender = commonService.createInstanceUser();

        listFriends.forEach(v -> {
            User receiver = UserMapper.INSTANCE.toUser(v);
            NotificationPostMessage noti = createNotificationFriendMessage(sender, receiver, postId);
            kafkaProducerService.sendPostMessage(topic, noti);
        });
    }

//    @Scheduled(fixedDelay = 10000) // Mỗi 60s chạy lại
    public void retryFailedPostNotifications() {
        try {
            System.out.println("aaaaaaaaaaaaaaaa");
            List<String> keys = redisService.scanKeys("retry:post:*");
            log.info("Retry gửi notification={}", keys);

            for (String key : keys) {
                String postId = redisService.get(key, String.class);
                if (postId != null) {
                    log.info("Retry gửi notification cho postId={}", postId);
                    try {
                        sendMessagesPostToFriendsSync(postId); // retry
                        redisService.delete(key); // thành công thì xóa khỏi Redis
                    } catch (Exception ex) {
                        log.error("Retry thất bại: {} ", ex.getMessage());
                        log.error("Retry thất bại cho postId={}, giữ lại trong Redis", postId);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Lỗi trong job retry notification", e);
        }
    }


    public PostResponse store(CreatePostRequest createPostRequest) {
        List<String> requestedTags = Optional.ofNullable(createPostRequest.getTags())
                .orElse(Collections.emptyList());
        storeTagsNonAvailable(requestedTags);

        CompletableFuture<List<Picture>> picsFuture = CompletableFuture.supplyAsync(() ->
                storePicsToCloudinary(createPostRequest.getPictures()));
        CompletableFuture.allOf(picsFuture).join();

        try {
            List<Tag> newTagsList = tagRepository.findByNameIn(requestedTags);
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

            Post savedPost = postRepository.save(newPost);
            sendMessagesPostToFriendsAsync(savedPost.getId());
            PostResponse p = PostMapper.INSTANCE.toPostResponse(savedPost);
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

    private Integer getNumberOfComments(List<UserPostInteraction> us) {
        return us.stream()
                .flatMap(u -> {
                    List<Comment> comments = commentRepository.findByUserPostInteraction(u).get();
                    return comments.stream();
                })
                .toList().size();
    }


    public List<PostResponse> list(int page, int size) {
        User user = commonService.createInstanceUser();
        Pageable p = PageRequest.of(page, size, Sort.by("createdAt").descending());
        List<Post> posts = postRepository.findByUserId(user.getId(), p).stream().toList();
        List<PostResponse> postResponses = posts.stream().map(PostMapper.INSTANCE::toPostResponse).toList();
        List<UserPostInteraction> interactions = userPostInteractionRepository
                .findByPostIn(posts)
                .orElse(Collections.emptyList());

        // Group theo postId để truy cập nhanh
        Map<String, List<UserPostInteraction>> interactionMap = interactions.stream()
                .collect(Collectors.groupingBy(u -> u.getPost().getId()));

        // Gán count likes/comments vào response
        IntStream.range(0, postResponses.size()).forEach(i -> {
            Post post = posts.get(i);
            PostResponse response = postResponses.get(i);
            List<UserPostInteraction> us = interactionMap.getOrDefault(post.getId(), Collections.emptyList());

            response.setCountLikes((int) us.stream().filter(u -> u.getReaction() != null).count());
            response.setCountComments(getNumberOfComments(us));
            us.stream()
                    .filter(u -> u.getUser().getId().equals(user.getId()) && u.getReaction() != null)
                    .findFirst()
                    .ifPresent(u -> {
                        Reaction r = u.getReaction();
                        ReactionResponse reactionResponse = ReactionResponse.builder()
                                .id(r.getId())
                                .reaction(r.getIconName().name())
                                .created_at(r.getCreatedAt())
                                .updated_at(r.getUpdatedAt())
                                .build();
                        response.setMyReaction(reactionResponse);
                    });
        });
        return postResponses;
    }

    public PostResponse retrieve(String postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXISTED));
        PostResponse p = PostMapper.INSTANCE.toPostResponse(post);
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

        // Xác định mediaType (giữ lại nếu null)
        MediaType mediaType = Objects.isNull(updatePostRequest.getMediaType())
                ? post.getMediaType()
                : MediaType.valueOf(updatePostRequest.getMediaType());

        // Cập nhật các trường cơ bản
        Optional.ofNullable(updatePostRequest.getTitle()).ifPresent(post::setTitle);
        Optional.ofNullable(updatePostRequest.getContent()).ifPresent(post::setContent);
        Optional.ofNullable(updatePostRequest.getIsPrivate()).ifPresent(post::setIsPrivate);
        Optional.of(mediaType).ifPresent(post::setMediaType);

        // Xử lý tags
        List<String> requestedTags = Optional.ofNullable(updatePostRequest.getTags())
                .orElse(Collections.emptyList());
        storeTagsNonAvailable(requestedTags);
        List<Tag> tags = tagRepository.findByNameIn(requestedTags);
        Optional.of(tags.isEmpty() ? post.getTags() : tags).ifPresent(post::setTags);

        // Xử lý hình ảnh
        try {
            // 1. Lấy danh sách URL ảnh cũ từ FE muốn giữ lại
            List<String> oldPictureUrls = Optional.ofNullable(updatePostRequest.getOldPictures())
                    .orElse(Collections.emptyList());

            // 2. Giữ lại các Picture có URL nằm trong oldPictureUrls
            List<Picture> picturesToKeep = post.getPics() != null
                    ? post.getPics().stream()
                    .filter(p -> oldPictureUrls.contains(p.getUrl()))
                    .toList()
                    : new ArrayList<>();

            // 3. Xoá các Picture không cần giữ lại
            List<Picture> picturesToDelete = post.getPics() != null
                    ? post.getPics().stream()
                    .filter(p -> !oldPictureUrls.contains(p.getUrl()))
                    .collect(Collectors.toList())
                    : new ArrayList<>();
            pictureRepository.deleteAll(picturesToDelete);

            // 4. Upload hình mới (an toàn với null)
            List<MultipartFile> pictures = Optional.ofNullable(updatePostRequest.getPictures())
                    .orElse(Collections.emptyList());
            List<Picture> newPictures = storePicsToCloudinary(pictures);
            newPictures.forEach(pic -> pic.setPost(post));
            pictureRepository.saveAll(newPictures);

            // 5. Gộp ảnh cũ và mới
            List<Picture> mergedPictures = new ArrayList<>();
            mergedPictures.addAll(picturesToKeep);
            mergedPictures.addAll(newPictures);

            // 6. Gán lại vào post
            if (post.getPics() == null) {
                post.setPics(new ArrayList<>());
            } else {
                post.getPics().clear();
            }
            post.getPics().addAll(mergedPictures);
        } catch (Exception e) {
            log.error("Error while updating picture", e);
            throw new AppException(ErrorCode.UPDATE_ERROR);
        }

        // Lưu và trả kết quả
        Post savedPost = postRepository.save(post);
        PostResponse p = PostMapper.INSTANCE.toPostResponse(savedPost);

//        // Lấy tương tác
//        List<UserPostInteraction> us = userPostInteractionRepository.findByPost(post)
//                .orElseGet(() -> {
//                    p.setCountComments(0);
//                    p.setCountLikes(0);
//                    return Collections.emptyList();
//                })
//                .stream()
//                .filter(u -> Objects.nonNull(u.getReaction()))
//                .toList();
//
//        if (!us.isEmpty()) {
//            p.setCountLikes(us.size());
//            p.setCountComments(this.getNumberOfComments(us));
//        }

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
