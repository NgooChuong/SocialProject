package com.social.postService.service;

import com.social.postService.dto.request.CreateCommentRequest;
import com.social.postService.dto.response.CommentResponse;
import com.social.postService.entity.Comment;
import com.social.postService.entity.Post;
import com.social.postService.entity.User;
import com.social.postService.entity.UserPostInteraction;
import com.social.postService.exception.AppException;
import com.social.postService.exception.ErrorCode;
import com.social.postService.mapper.CommentMapper;
import com.social.postService.repository.CommentRepository;
import com.social.postService.repository.PostRepository;
import com.social.postService.repository.UserPostInteractionRepository;
import com.social.postService.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentService {
    CommentRepository commentRepository;
    PostRepository postRepository;
    UserPostInteractionRepository userPostInteractionRepository;
    CommonService commonService;
    private final UserRepository userRepository;


    public List<CommentResponse> list (String postId, int page, int size) {
        Post p = postRepository.findById(postId).orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXISTED));
        Pageable pageable =  PageRequest.of(page, size, Sort.by("id").descending());
        Page<UserPostInteraction> us = userPostInteractionRepository.findByPost(p, pageable);
        List<Comment> cmts = us.stream()
                .flatMap(u -> {
                    List<Comment> comments = commentRepository.findByUserPostInteraction(u).get();
                    return comments.stream();
                })
                .collect(Collectors.toList());
        return CommentMapper.INSTANCE.toCommentResponses(cmts);
    }

    public CommentResponse store(CreateCommentRequest createCommentRequest) {
//        User authenUser = commonService.createInstanceUser();
        User authenUser = userRepository.findById(createCommentRequest.getUserId()).orElse(null);
        Post curPost = postRepository.findById(createCommentRequest.getPostId()).orElseThrow(
                () -> new AppException(ErrorCode.POST_NOT_EXISTED)
        );
        UserPostInteraction u = userPostInteractionRepository.findByPostAndUser(curPost, authenUser).orElseGet(
                () -> userPostInteractionRepository.save(
                        UserPostInteraction.builder().post(curPost).user(authenUser).build()
                )
        );
        try {
            Comment comment = commentRepository.save(
                    Comment.builder()
                            .content(createCommentRequest.getContent())
                            .userPostInteraction(u).build()
            );
            return  CommentMapper.INSTANCE.toCommentResponse(comment);

        } catch (Exception e) {
            throw new AppException(ErrorCode.COMMENT_CREATION_FAILED);
        }

    }

    public CommentResponse update(String commentId, String updateCommentRequest) {
        Comment cmt = commentRepository.findById(commentId).orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_EXISTED));
        if (cmt.getUserPostInteraction().getUser().getId().equals(commonService.createInstanceUser().getId())) {
            cmt.setContent(updateCommentRequest);
            try {
                Comment comment = commentRepository.save(cmt);
                return CommentMapper.INSTANCE.toCommentResponse(comment);
            } catch (Exception e) {
                throw new AppException(ErrorCode.COMMENT_UPDATE_FAILED);
            }
        }
        else throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    public void delete(String commentId) {
        Comment cmt = commentRepository.findById(commentId).orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_EXISTED));
        if (cmt.getUserPostInteraction().getUser().getId().equals(commonService.createInstanceUser().getId())) {
            try {
                commentRepository.delete(cmt);
            } catch (Exception e) {
                throw new AppException(ErrorCode.COMMENT_DELETE_FAILED);
            }
        }
        else throw new AppException(ErrorCode.UNAUTHORIZED);
    }
}
