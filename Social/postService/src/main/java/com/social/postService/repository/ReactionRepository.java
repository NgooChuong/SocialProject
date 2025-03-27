package com.social.postService.repository;

import com.social.postService.entity.Reaction;
import com.social.postService.enums.ReactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, String> {
    Optional<Reaction> findByIconName(ReactionType iconName);
}
