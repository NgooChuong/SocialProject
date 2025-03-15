package com.social.profileservice.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.social.profileservice.dto.request.InformationCreateRequest;
import com.social.profileservice.dto.request.InformationRequest;
import com.social.profileservice.dto.response.InformationResponse;
import com.social.profileservice.entity.Information;
import com.social.profileservice.exception.AppException;
import com.social.profileservice.exception.ErrorCode;
import com.social.profileservice.mapper.InformationMapper;
import com.social.profileservice.repository.InformationRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InformationService {
    InformationRepository informationRepository;
    InformationMapper informationMapper;
    Cloudinary cloudinary;

    public InformationResponse createProfile(InformationCreateRequest request) {
        if (!(request.getFile() == null)) {
            try {
                Map res = this.cloudinary.uploader().upload(request.getFile().getBytes(),
                        ObjectUtils.asMap("resource_type", "auto"));

                request.setAvatar(res.get("secure_url").toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        // mapper k dc do cái request k có avatar
        Information userProfile = informationMapper.toInformation(request);
        informationRepository.save(userProfile);
        return informationMapper.toInformationResponse(userProfile);
    }

    public InformationResponse getProfile(String id) {
        Information userProfile =
                informationRepository.findById(id).orElseThrow(() -> new RuntimeException("Profile not found"));

        return informationMapper.toInformationResponse(userProfile);
    }

    public InformationResponse updateUser(String userId, InformationCreateRequest request) {// dang loi tu nhien tao user moi
        Information user = informationRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        if (!(request.getFile() == null)) {
            log.info(request.getFile().toString());
            try {
                Map res = this.cloudinary.uploader().upload(request.getFile().getBytes(),
                        ObjectUtils.asMap("resource_type", "auto"));
                request.setAvatar(res.get("secure_url").toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setDob(request.getDob());
        user.setAvatar(request.getAvatar());
        informationRepository.save(user);
        return informationMapper.toInformationResponse(user);
    }

    public void deleteUser(String userId) {
        informationRepository.deleteById(userId);
    }

}
