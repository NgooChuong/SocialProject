package com.social.identityservice.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.social.identityservice.dto.request.ClientCreationRequest;
import com.social.identityservice.dto.request.UserCreationRequest;
import com.social.identityservice.dto.request.UserUpdateRequest;
import com.social.identityservice.dto.response.UserResponse;
import com.social.identityservice.entity.User;
import com.social.identityservice.enums.Role;
import com.social.identityservice.enums.StatusAccount;
import com.social.identityservice.exception.AppException;
import com.social.identityservice.exception.ErrorCode;
import com.social.identityservice.mapper.ClientMapper;
import com.social.identityservice.mapper.UserMapper;
import com.social.identityservice.repository.UserRepository;
import com.social.identityservice.repository.httpclient.UserUnauthenClient;
import feign.FeignException;
import jakarta.annotation.Priority;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    UserUnauthenClient userUnauthenClent;
    ClientMapper clientMapper;
    Cloudinary cloudinary;
    private void validateUserExistence(UserCreationRequest request) {
        // Chỉ kiểm tra khi thuộc tính không null
        if (request.getUsername() != null && userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        if (request.getGoogle_id() != null && userRepository.existsByGoogleId(request.getGoogle_id())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        if (request.getPhone() != null && userRepository.existsByPhone(request.getPhone())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
    }
    private User buildUserFromRequest(UserCreationRequest request) {
        User user = userMapper.toUser(request);
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        user.setCreateAt(new Date());
        user.setLoginAt(new Date());
        user.setRole(Role.USER.name());
        user.setStatus(StatusAccount.ACTIVE.name());
        user.setLocation(request.getLocation());
        if (request.getGoogle_id() != null) {
            user.setGoogleId(request.getGoogle_id());
            user.setEmail(request.getEmail());
        }
        return user;
    }
    // maxAttempts= 3 is default
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000),retryFor = FeignException.class)
    void createUserProfile(User user, UserCreationRequest request) throws FeignException {
        log.info("abc:{}", request.getFile());
        String avatar = uploadFileToCloudinary(request.getFile());
        ClientCreationRequest clientCreationRequest = clientMapper.toClientCreationRequest(request);
        clientCreationRequest.setUserId(user.getUserId());
        clientCreationRequest.setAvatar(avatar);
        clientCreationRequest.setLocation(request.getLocation());
        // Gọi service profile
        try {
            userUnauthenClent.createUser(clientCreationRequest);
        }
        catch (FeignException e) {
            throw new AppException(ErrorCode.PROFILE_CREATION_FAILED);
        }
    }
//    @Recover
//    void recoverProfileCreation(FeignException e, User user, UserCreationRequest request) {
//        // Compensation: Xóa user nếu profile creation thất bại sau 3 lần retry
////        userRepository.delete(user);
//        System.out.println("Recover called with exception: " + e.getClass().getName());
//    }
public String uploadFileToCloudinary(MultipartFile file) {
    if (Objects.nonNull(file)) {
        try {
            Map res = this.cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("resource_type", "auto"));

            return res.get("secure_url").toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    return null;
}
    public UserResponse createUser(UserCreationRequest request){
        validateUserExistence(request); // Kiểm tra tồn tại
        User user = buildUserFromRequest(request); // Tạo user
        // xử lý thêm roll back khi 1 service profile fail
        user = userRepository.save(user);
        log.info("createUser:{}",request.getFile());
        createUserProfile(user, request); // Tạo profile ở service khác
        return userMapper.toUserResponse(user);
    }

    public void updateUser(String userId, UserUpdateRequest request) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        // update ben identitydb
        userMapper.updateUser(user, request);
        userRepository.save(user); // luu username
    }

    public void deleteUser(String userId){
        userRepository.deleteById(userId);
    }

    public List<UserResponse> getUsers(){
        return userRepository.findAll().stream()
                .map(userMapper::toUserResponse).toList();
    }

    public UserResponse getUser(String id){
        return userMapper.toUserResponse(userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
    }
}
