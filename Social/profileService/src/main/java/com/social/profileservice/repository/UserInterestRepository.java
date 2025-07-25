package com.social.profileservice.repository;

import com.social.profileservice.entity.UserInterest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserInterestRepository extends JpaRepository<UserInterest, String> {
    @Query("SELECT ui.interest.name FROM UserInterest ui WHERE ui.userId = :userId")
    List<String> findInterestNamesById(String userId);}
