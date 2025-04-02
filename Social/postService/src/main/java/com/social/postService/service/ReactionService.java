package com.social.postService.service;

import com.social.postService.dto.request.UpdateReactionRequest;
import com.social.postService.dto.response.UserReactionResponse;
import com.social.postService.entity.Post;
import com.social.postService.entity.Reaction;
import com.social.postService.entity.User;
import com.social.postService.entity.UserPostInteraction;
import com.social.postService.enums.ReactionType;
import com.social.postService.exception.AppException;
import com.social.postService.exception.ErrorCode;
import com.social.postService.mapper.UserPostReactionMapper;
import com.social.postService.repository.PostRepository;
import com.social.postService.repository.ReactionRepository;
import com.social.postService.repository.UserPostInteractionRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReactionService {
    ReactionRepository reactionRepository;
    PostRepository postRepository;
    UserPostInteractionRepository userPostInteractionRepository;
    CommonService commonService;

    private Post getPost(String postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> {
                    log.warn("Post with id {} not found", postId);
                    return new AppException(ErrorCode.POST_NOT_EXISTED);
                });
    }

    public boolean isInReactionTypeEnum(String value) {
        try {
            ReactionType.valueOf(value.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private Reaction getOrcreateReaction(String iconName) {
        if (iconName == null || iconName.trim().isEmpty() || !isInReactionTypeEnum(iconName))
            throw new AppException(ErrorCode.INVALID_REACTION_TYPE);
        try {
            iconName = iconName.trim().toUpperCase();
            ReactionType reactionType = ReactionType.valueOf(iconName);
            Optional<Reaction> optionalReaction = reactionRepository.findByIconName(reactionType);
            if (optionalReaction.isPresent()) {
                return optionalReaction.get();
            }
            ;
            return Reaction.builder()
                    .iconName(reactionType)
                    .build();
        } catch (IllegalArgumentException e) {
            log.warn("Invalid reaction type: {}", iconName);
            throw new AppException(ErrorCode.INVALID_REACTION_TYPE);
        }
    }

    public List<UserReactionResponse> getUserReactionByType(String postId, String type, int page, int size) {
        if (type == null || type.trim().isEmpty()) {
            throw new AppException(ErrorCode.INVALID_REACTION_TYPE);
        }
        Post p = getPost(postId);
        if (p == null) {
            throw new AppException(ErrorCode.POST_NOT_EXISTED);
        }
        Page<UserPostInteraction> uPI;
        Pageable pageable =  PageRequest.of(page, size, Sort.by("id").descending());
        String trimmedType = type.trim();
        if (trimmedType.equalsIgnoreCase("ALL")) {
            uPI = userPostInteractionRepository.findByPost(p,pageable);
        } else if (isInReactionTypeEnum(trimmedType)) {
            Reaction r = getOrcreateReaction(trimmedType);
            uPI = userPostInteractionRepository.findByPostAndReactionId(p, r.getId(), pageable);
        } else {
            throw new AppException(ErrorCode.INTERACTION_NOT_FOUND);
        }

        return uPI.getContent().stream()
                .filter(u ->  Objects.nonNull(u.getReaction()))
                .map(UserPostReactionMapper.INSTANCE::toUserReaction)
                .toList();
    }

    public Boolean storeOrUpdate(String postId, UpdateReactionRequest updateReactionRequest) {
        Objects.requireNonNull(updateReactionRequest, "UpdateReactionRequest must not be null");
        try {
            Post post = getPost(postId);
            User currentUser = commonService.createInstanceUser();
            Reaction reaction = getOrcreateReaction(updateReactionRequest.getIconName());

            // Tìm tương tác hiện tại
            Optional<UserPostInteraction> existingInteractionOpt =
                    userPostInteractionRepository.findByPostAndUser(post, currentUser);

            UserPostInteraction userPostInteraction;
            boolean isUpdate = existingInteractionOpt.isPresent();

            if (isUpdate) {
                // Cập nhật tương tác cũ
                log.info("1");
                userPostInteraction = existingInteractionOpt.get();
                userPostInteraction.setReaction(reaction);
                log.info("Updating reaction for postId: {}, userId: {}", postId, currentUser.getId());
            } else {
                // Tạo mới tương tác
                userPostInteraction = UserPostInteraction.builder()
                        .user(currentUser)
                        .reaction(reaction)
                        .post(post)
                        .build();
                log.info("Storing new reaction for postId: {}, userId: {}", postId, currentUser.getId());
            }
            userPostInteractionRepository.save(userPostInteraction);
            return true;
        } catch (Exception e) {
            log.error("Failed to store/update reaction for", e);
            return false;
        }
    }

    public Boolean delete(String postId) {
        // Lấy người dùng hiện tại
        try {
            User currentUser = commonService.createInstanceUser();

            // Lấy Post từ postId
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> {
                        log.warn("Post with id {} not found", postId);
                        return new AppException(ErrorCode.POST_NOT_EXISTED);
                    });

            // Tìm UserPostInteraction dựa trên Post và User
            UserPostInteraction userPostInteraction = userPostInteractionRepository.findByPostAndUser(post, currentUser)
                    .orElseThrow(() -> {
                        log.warn("No interaction found for postId: {} and userId: {}", postId, currentUser.getId());
                        return new AppException(ErrorCode.INTERACTION_NOT_FOUND);
                    });

            // Xóa UserPostInteraction
            userPostInteractionRepository.delete(userPostInteraction);
            log.info("Deleted interaction for postId: {} and userId: {}", postId, currentUser.getId());
            return true;
        } catch (Exception e) {
            log.error("Failed to delete reaction for", e);
            return false;
        }
    }
}
