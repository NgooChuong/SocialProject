package com.social.postService.service;

import com.social.postService.entity.User;
import com.social.postService.exception.AppException;
import com.social.postService.exception.ErrorCode;
import com.social.postService.repository.UserRepository;
import com.social.postService.repository.httpClient.ProfileClient;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CommonService {
    UserRepository userRepository;
    ProfileClient profileClient;

    public User createInstanceUser() { // current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        String userId = jwt.getClaimAsString("userId"); // Lấy userId từ claims
        log.info("userId:{}", userId);
        return userRepository.findById(userId).orElseGet(() -> {
            String username = jwt.getClaimAsString("sub");
            try {
                String avatar = profileClient.getProfileByUserId(userId).getResult().getAvatar();
                return userRepository.save(User.builder().id(userId).username(username).avatar(avatar).build());
            } catch (Exception e) {
                throw new AppException(ErrorCode.NOT_FIND_SERVER);
            }
        });
    }
}
