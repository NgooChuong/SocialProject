package com.social.postService.repository;

import com.social.postService.entity.Comment;
import com.social.postService.entity.Post;
import com.social.postService.entity.UserPostInteraction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {
    Optional<List<Comment>> findByUserPostInteraction(UserPostInteraction u);
    Page<Comment> findByUserPostInteractionPost(Post post, Pageable pageable);
}
