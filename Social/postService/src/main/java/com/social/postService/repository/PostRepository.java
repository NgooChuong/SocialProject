package com.social.postService.repository;

import com.social.postService.entity.Post;
import com.social.postService.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Repository
public interface PostRepository extends JpaRepository<Post, String> {
    Page<Post> findByUserId(String userId, Pageable pageable);

    @Query("""
    SELECT p FROM Post p\s
    WHERE p.user.id IN :userIds\s
      AND (:before IS NULL OR p.createdAt < :before)
    ORDER BY p.createdAt DESC
""")
    List<Post> findByUserIdInBefore(
            @Param("userIds") List<String> userIds,
            @Param("before") LocalDateTime before
    );
    @Query("""
    SELECT DISTINCT p FROM Post p\s
    JOIN p.tags t\s
    WHERE t.name IN :tagNames\s
      AND p.user.id <> :userId
      AND (:before IS NULL OR p.createdAt < :before)
    ORDER BY p.createdAt DESC
""")
    List<Post> findByTagNamesExcludeUserBefore(
            @Param("tagNames") List<String> tagNames,
            @Param("userId") String userId,
            @Param("before") LocalDateTime before
    );}

