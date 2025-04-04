package com.social.friendService.service;

import com.social.friendService.dto.request.FriendStatusRequest;
import com.social.friendService.dto.response.UserResponse;
import com.social.friendService.entity.FriendShips;
import com.social.friendService.entity.User;
import com.social.friendService.enums.FriendshipStatus;
import com.social.friendService.exception.AppException;
import com.social.friendService.exception.ErrorCode;
import com.social.friendService.mapper.UserMapper;
import com.social.friendService.repository.FriendShipRepository;
import com.social.friendService.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FriendService {

    UserRepository userRepository;
    CommonService commonService;
    FriendShipRepository friendshipRepository;

    //Gui request ket ban
    public void sendAddFriendRequest(String friendId) {
        User user = commonService.getOrcreateInstanceAuthUser();
        User friend = commonService.getOrcreateInstanceUser(friendId);

        // Kiểm tra không thêm chính mình
        if (user.getId().equals(friend.getId())) {
            throw new AppException(ErrorCode.NOT_ADD_FRIEND_YOURSELF);
        }
        Optional<FriendShips> fs = friendshipRepository.findByUserAndFriend(user, friend);
        // Kiểm tra xem đã là bạn bè chưa
        boolean alreadyFriends = fs.map(f -> f.getStatus() == FriendshipStatus.ACCEPTED).orElse(false);
        if (alreadyFriends) {
            throw new AppException(ErrorCode.ALREADY_FRIEND);
        }

        try {
            if (fs.isPresent()) {
                fs.get().setStatus(FriendshipStatus.PENDING);
                friendshipRepository.save(fs.get());
            } else {
                // Tạo một Friendship mới
                FriendShips friendship = FriendShips.builder().user(user).friend(friend).status(FriendshipStatus.PENDING).build();
                friendshipRepository.save(friendship);
            }
        } catch (Exception e) {
            throw new AppException(ErrorCode.ADD_FRIEND_ERROR);
        }
    }

    public void acceptOrReject(String friendId, FriendStatusRequest status) {
        User user = commonService.getOrcreateInstanceAuthUser();
        User friend = commonService.getOrcreateInstanceUser(friendId);
        friendshipRepository.findByUserAndFriend(user, friend).ifPresent((u) -> {
            FriendshipStatus fs;
            try {
                fs = FriendshipStatus.valueOf(status.getType());

            } catch (Exception e) {
                throw new AppException(ErrorCode.FRIENDSHIP_STATUS_NOT_VALID);
            }
            if (u.getStatus() == FriendshipStatus.PENDING) {
                switch (fs) {
                    case ACCEPTED -> u.setStatus(FriendshipStatus.ACCEPTED);
                    case REJECTED -> u.setStatus(FriendshipStatus.REJECTED);
                    default -> throw new AppException(ErrorCode.FRIENDSHIP_STATUS_NOT_VALID);
                }
                try {
                    friendshipRepository.save(u);
                } catch (Exception e) {
                    throw new AppException(ErrorCode.ACCEPT_FRIEND_ERROR);
                }
            } else if (u.getStatus() == FriendshipStatus.ACCEPTED) throw new AppException(ErrorCode.ALREADY_FRIEND);
            else throw new AppException(ErrorCode.ALREADY_REJECT);
        });
    }

    // Huy ket ban
    public void removeFriend(String friendId) {
        User user = commonService.getOrcreateInstanceAuthUser(); // Người dùng hiện tại
        User friend = userRepository.findById(friendId).orElseThrow(() -> new AppException(ErrorCode.NOT_FIND_FRIEND));
        // Tìm bản ghi Friendship giữa user và friend
        FriendShips friendship = friendshipRepository.findByUserAndFriend(user, friend).orElseThrow(() -> new AppException(ErrorCode.NOT_FRIEND_RELATIONSHIP));

        // Kiểm tra trạng thái (chỉ xóa nếu đã là bạn bè - ACCEPTED)
        if (friendship.getStatus() != FriendshipStatus.ACCEPTED) {
            throw new AppException(ErrorCode.NOT_FRIEND_RELATIONSHIP);
        }

        try {
            // Xóa bản ghi Friendship
            friendshipRepository.delete(friendship);
        } catch (Exception e) {
            throw new AppException(ErrorCode.REMOVE_FRIEND_ERROR);
        }
    }

    public Page<UserResponse> getFriends(int page, int size, String friendStatusRequest) {
        Pageable pageable = PageRequest.of(page, size);
        User user = commonService.getOrcreateInstanceAuthUser();
        try {
            FriendshipStatus fs = FriendshipStatus.valueOf(friendStatusRequest);
            List<FriendShips> friends = friendshipRepository.findFriendShipsByStatusAndUser(fs, user, pageable);
            List<UserResponse> friendResponses = friends.stream().map(f -> UserMapper.INSTANCE.toUserResponse(f.getFriend())).toList();
            return new PageImpl<>(friendResponses, pageable, friendResponses.size());
        } catch (Exception e) {
            throw new AppException(ErrorCode.FRIENDSHIP_STATUS_NOT_VALID);
        }
    }
}