package com.social.profileservice.repository;

import com.social.profileservice.entity.Information;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InformationRepository extends JpaRepository<Information, String> {
    Optional<Information> findByUserId(String id);
}
