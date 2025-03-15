package com.social.identityservice.service;

import com.social.identityservice.dto.request.UserCreationRequest;
import com.social.identityservice.dto.request.UserUpdateRequest;
import com.social.identityservice.dto.response.InformationResponse;
import com.social.identityservice.dto.response.UserResponse;
import com.social.identityservice.entity.User;
import com.social.identityservice.enums.Role;
import com.social.identityservice.enums.StatusAccount;
import com.social.identityservice.exception.AppException;
import com.social.identityservice.exception.ErrorCode;
import com.social.identityservice.mapper.UserMapper;
import com.social.identityservice.repository.UserRepository;
import com.social.identityservice.repository.httpclient.UserClient;
import com.social.identityservice.repository.httpclient.UserUnauthenClent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    UserClient userClient;
    UserUnauthenClent userUnauthenClent;


    public UserResponse createUser(UserCreationRequest request){
        if (userRepository.existsByUsername(request.getUsername())||
            userRepository.existsByEmail(request.getEmail())||
            userRepository.existsByGoogleId(request.getGoogle_id())||
            userRepository.existsByPhone(request.getPhone()))
            throw new AppException(ErrorCode.USER_EXISTED);

        request.setPassword(passwordEncoder.encode(request.getPassword()));

         InformationResponse qlbdxResponse = userUnauthenClent.createUser(request);

        User user = userMapper.toUser(request);
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        if (request.getPhone()!=null)
            user.setPhone(request.getPhone());
        user.setCreateAt(new Date());
        user.setLoginAt(new Date());
        user.setRole(Role.USER.name());
        user.setStatus(StatusAccount.ACTIVE.name());
        if (request.getGoogle_id() != null) {
            user.setGoogleId(request.getGoogle_id());
            user.setEmail(request.getGoogle_id());
        }
        user =userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

//    public InformationResponse updateUser(String userId, UserUpdateRequest request) {
//        User user = userRepository.findByUserId(userId)
//                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
//        // update ben identitydb
//        userMapper.updateUser(user, request);
//        userRepository.save(user); // luu username
//        InformationResponse updatedUser = userClient.updateUser(userId,request);
//        return updatedUser;
//    }

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
    public boolean existUser(String username){
        return userRepository.existsByUsername(username);
    }
}
