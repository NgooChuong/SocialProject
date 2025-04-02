package com.social.postService.repository;

import com.social.postService.entity.Post;
import com.social.postService.entity.User;
import com.social.postService.entity.UserPostInteraction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserPostInteractionRepository extends JpaRepository<UserPostInteraction, Long> {
    Optional<UserPostInteraction> findByPostAndUser(Post p, User user);
    Page<UserPostInteraction> findByPostAndReactionId(Post post, String reaction_id, Pageable pageable);
    Optional<List<UserPostInteraction>> findByPost(Post post);
    Page<UserPostInteraction> findByPost(Post post, Pageable pageable);

}
