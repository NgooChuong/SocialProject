package com.social.friendService.service;

import com.social.friendService.dto.response.IdentifyResponse;
import com.social.friendService.dto.response.InformationResponse;
import com.social.friendService.entity.User;
import com.social.friendService.exception.AppException;
import com.social.friendService.exception.ErrorCode;
import com.social.friendService.repository.UserRepository;
import com.social.friendService.repository.httpClient.IdentifyClient;
import com.social.friendService.repository.httpClient.ProfileClient;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CommonService {
    UserRepository userRepository;
    ProfileClient profileClient;
    IdentifyClient identifyClient;
    TaskExecutor taskExecutor;
    public CommonService(UserRepository userRepository,
                         ProfileClient profileClient,
                         IdentifyClient identifyClient,
                         @Qualifier("taskExecutorCustom") TaskExecutor taskExecutor) {
        this.userRepository = userRepository;
        this.profileClient = profileClient;
        this.identifyClient = identifyClient;
        this.taskExecutor = taskExecutor;
    }

    public User getOrcreateInstanceAuthUser() { // current user
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

    public User getOrcreateInstanceUser(String userId) { // another user
        return userRepository.findById(userId).orElseGet(() -> {
            try {
                // them dung context
                CompletableFuture<InformationResponse> profileFuture = CompletableFuture
                        .supplyAsync(() ->  profileClient.getProfileByUserId(userId).getResult(), taskExecutor);
                CompletableFuture<IdentifyResponse> identifyFuture = CompletableFuture
                        .supplyAsync(() -> identifyClient.getUser(userId).getResult(), taskExecutor);
                CompletableFuture.allOf(profileFuture, identifyFuture).join();
                InformationResponse resInfo = profileFuture.get();
                IdentifyResponse resIdentify = identifyFuture.get();
                log.info("res:{}", resInfo);
                log.info("res:{}", resIdentify);
                return userRepository.save(User.builder()
                        .id(userId)
                        .username(resIdentify.getUsername())
                        .avatar(resInfo.getAvatar()).build());
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new AppException(ErrorCode.NOT_FIND_SERVER);
            }
        });
    }

}
