package com.social.identityservice.repository;

import com.social.identityservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByGoogleId(String google_id);
    boolean existsByPhone(String phone);
    Optional<User> findByUsername(String username);
    Optional<User> findByPhone(String phone);
    Optional<User> findByGoogleId(String google_id);
    Optional<User> findByEmail(String email);
    Optional<User> findByUserId(String id);
}
