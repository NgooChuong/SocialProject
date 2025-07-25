package com.social.notificationService.service;

import com.social.notificationService.exception.AppException;
import com.social.notificationService.exception.ErrorCode;
import com.social.notificationService.repository.UserRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CommonService {
    UserRepository userRepository;
    TaskExecutor taskExecutor;
    public CommonService(UserRepository userRepository,
                         @Qualifier("taskExecutorCustom") TaskExecutor taskExecutor) {
        this.userRepository = userRepository;
        this.taskExecutor = taskExecutor;
    }
    public static String getUserIdFromJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        return jwt.getClaimAsString("userId");
    }
}
