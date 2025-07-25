package com.social.profileservice.repository;

import com.social.profileservice.entity.Interest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterestRepository extends JpaRepository<Interest, String> {
}
