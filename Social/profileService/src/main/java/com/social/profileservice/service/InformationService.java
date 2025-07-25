package com.social.profileservice.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.social.profileservice.dto.request.ApiResponse;
import com.social.profileservice.dto.request.InformationCreateRequest;
import com.social.profileservice.dto.request.InformationUpdateRequest;
import com.social.profileservice.dto.response.InformationResponse;
import com.social.profileservice.dto.response.InformationScore;
import com.social.profileservice.entity.Information;
import com.social.profileservice.exception.AppException;
import com.social.profileservice.exception.ErrorCode;
import com.social.profileservice.mapper.InformationMapper;
import com.social.profileservice.repository.InformationRepository;
import com.social.profileservice.repository.UserInterestRepository;
import com.social.profileservice.repository.httpClient.FriendClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InformationService {
    InformationRepository informationRepository;
    InformationMapper informationMapper;
    Cloudinary cloudinary;
    UserInterestRepository userInterestRepository;
    FriendClient friendClient;

    private double calculateScore(List<String> interests1, List<String> interests2, String location1, String location2) {
        double interestScore = jaccardSimilarity(interests1, interests2);
        double locationScore = location1.equalsIgnoreCase(location2) ? 1.0 : 0.0;

        return 0.7 * interestScore + 0.3 * locationScore;
    }

    private double jaccardSimilarity(List<String> a, List<String> b) {
        Set<String> setA = new HashSet<>(a);
        Set<String> setB = new HashSet<>(b);
        Set<String> intersection = new HashSet<>(setA);
        intersection.retainAll(setB);
        Set<String> union = new HashSet<>(setA);
        union.addAll(setB);
        if (union.isEmpty()) return 0.0;
        return (double) intersection.size() / union.size();
    }

    public Page<InformationResponse> recommendUsers(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<String> friendIds = friendClient.getAllYourFriends().getResult();

        // Lấy thông tin của người dùng hiện tại
        Information currentUser = informationRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<String> currentUserInterests = userInterestRepository.findInterestNamesById(currentUser.getId());
        String currentLocation = currentUser.getLocation();

        // Lấy tất cả user khác, phân trang
        Page<Information> otherUsersPage = informationRepository.findAllByUserIdNot(currentUser.getUserId(), pageable);
        List<InformationScore> scoredList = otherUsersPage.getContent().stream()
                .filter(other -> !friendIds.contains(other.getUserId()))
                .map(other -> {
                    List<String> otherInterests = userInterestRepository.findInterestNamesById(other.getId());
                    double score = calculateScore(currentUserInterests, otherInterests, currentLocation, other.getLocation());
                    return new InformationScore(other, score);
                })
                .filter(infoScore -> infoScore.getScore() > 0)
                .sorted(Comparator.comparingDouble(InformationScore::getScore).reversed())
                .toList();

        // Chuyển đổi sang response
        List<InformationResponse> responses = scoredList.stream()
                .map(scored -> {
                    InformationResponse res = informationMapper.toInformationResponse(scored.getInformation());
                    res.setScore(scored.getScore());
                    return res;
                })
                .toList();

        return new PageImpl<>(responses, pageable, otherUsersPage.getTotalElements());
    }

    public InformationResponse createProfile(InformationCreateRequest request) {
        log.info("Create Profile:{}", request);
        Information userProfile = informationMapper.toInformation(request);
        log.info("Create userProfile:{}", userProfile);
        informationRepository.save(userProfile);
        return informationMapper.toInformationResponse(userProfile);
    }

    public InformationResponse getProfile(String id) {
        Information userProfile =
                informationRepository.findById(id).orElseThrow(() -> new RuntimeException("Profile not found"));

        return informationMapper.toInformationResponse(userProfile);
    }

    public Page<InformationResponse> getAllInformationPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Information> informationPage = informationRepository.findAll(pageable);
        return informationPage.map(informationMapper::toInformationResponse);
    }

    public InformationResponse getProfileByUserId(String id) {
        Information userProfile =
                informationRepository.findByUserId(id).orElseThrow(() -> new RuntimeException("Profile not found"));
        return informationMapper.toInformationResponse(userProfile);
    }

    public InformationResponse updateUser(String userId, InformationUpdateRequest request) {// dang loi tu nhien tao user moi
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
