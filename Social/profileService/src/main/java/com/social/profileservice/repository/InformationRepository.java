package com.social.profileservice.repository;

import com.social.profileservice.entity.Information;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

    @Repository
    public interface InformationRepository extends JpaRepository<Information, String> {
        @EntityGraph(attributePaths = {"userInterests", "userInterests.interest"})
        Optional<Information> findByUserId(String id);
        @NonNull
        Page<Information> findAll(@NonNull Pageable pageable);
        Page<Information> findAllByUserIdNot(String id, Pageable pageable);

    }
