package com.social.friendService.service;

import com.social.friendService.dto.request.FriendStatusRequest;
import com.social.friendService.dto.request.message.NotificationFriendMessage;
import com.social.friendService.dto.request.message.UserMessage;
import com.social.friendService.dto.response.UserResponse;
import com.social.friendService.entity.FriendShips;
import com.social.friendService.entity.User;
import com.social.friendService.enums.FriendshipStatus;
import com.social.friendService.exception.AppException;
import com.social.friendService.exception.ErrorCode;
import com.social.friendService.mapper.UserMapper;
import com.social.friendService.repository.FriendShipRepository;
import com.social.friendService.repository.UserRepository;
import com.social.friendService.service.messageproducer.KafkaProducerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@Transactional
public class FriendService {

    private final UserRepository userRepository;
    private final CommonService commonService;
    private final FriendShipRepository friendshipRepository;
    private final KafkaProducerService kafkaProducerService;
    @Value("${kafka.topic.add}")
    private String TOPIC_FRIEND_ADD;
    @Value("${kafka.topic.acp}")
    private String TOPIC_FRIEND_ACP;

    public FriendService(UserRepository userRepository,
                         CommonService commonService,
                         FriendShipRepository friendshipRepository,
                         KafkaProducerService kafkaProducerService) {
        this.userRepository = userRepository;
        this.commonService = commonService;
        this.friendshipRepository = friendshipRepository;
        this.kafkaProducerService = kafkaProducerService;
    }


    private UserMessage createUserMessage(User user) {
        return UserMessage.builder()
                .id(user.getId())
                .username(user.getUsername())
                .avatar(user.getAvatar()).build();
    }

    private NotificationFriendMessage createNotificationFriendMessage(User user, User friend) {
        UserMessage sender = createUserMessage(user);
        UserMessage receiver = createUserMessage(friend);
        return NotificationFriendMessage.builder()
                .sender(sender)
                .receiver(receiver)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();
    }

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
        if (alreadyFriends) throw new AppException(ErrorCode.ALREADY_FRIEND);


        try {
            if (fs.isPresent()) {
                fs.get().setStatus(FriendshipStatus.PENDING);
                friendshipRepository.save(fs.get());
            } else {
                // Tạo một Friendship mới
                FriendShips friendship = FriendShips.builder().user(user).friend(friend).status(FriendshipStatus.PENDING).build();
                friendshipRepository.save(friendship);
            }
            NotificationFriendMessage notificationFriendMessage = createNotificationFriendMessage(user, friend);
            kafkaProducerService.sendFriendMessage(TOPIC_FRIEND_ADD, notificationFriendMessage);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new AppException(ErrorCode.ADD_FRIEND_ERROR);
        }
    }

    public void acceptOrReject(String friendId, FriendStatusRequest status) {
        User user = commonService.getOrcreateInstanceAuthUser();
        User friend = commonService.getOrcreateInstanceUser(friendId);
        friendshipRepository.findByUserAndFriend(friend, user).ifPresent((u) -> {
            FriendshipStatus fs;
            try {
                fs = FriendshipStatus.valueOf(status.getType());
            } catch (Exception e) {
                throw new AppException(ErrorCode.FRIENDSHIP_STATUS_NOT_VALID);
            }
            System.out.println(status);
            if (u.getStatus() == FriendshipStatus.PENDING) {
                switch (fs) {
                    case ACCEPTED -> {
                        u.setStatus(FriendshipStatus.ACCEPTED);
                        friendshipRepository.save(u);
                        // Gửi message khi chấp nhận
                        NotificationFriendMessage notificationFriendMessage = createNotificationFriendMessage(user, friend);
                        kafkaProducerService.sendFriendMessage(TOPIC_FRIEND_ACP, notificationFriendMessage);
                    }
                    case REJECTED -> {
                        u.setStatus(FriendshipStatus.REJECTED);
                        friendshipRepository.save(u);
                    }
                    default -> throw new AppException(ErrorCode.FRIENDSHIP_STATUS_NOT_VALID);
                }
            } else if (u.getStatus() == FriendshipStatus.ACCEPTED) {
                throw new AppException(ErrorCode.ALREADY_FRIEND);
            } else {
                throw new AppException(ErrorCode.ALREADY_REJECT);
            }
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

            // Chạy hai truy vấn đồng bộ
            List<FriendShips> friendsByUser = friendshipRepository.findFriendShipsByStatusAndUser(fs, user, pageable);
            List<FriendShips> friendsByFriend = friendshipRepository.findFriendShipsByStatusAndFriend(fs, user, pageable);

            // Nối hai danh sách
            List<FriendShips> friends = new ArrayList<>();
            friends.addAll(friendsByUser);
            friends.addAll(friendsByFriend);

            // Đếm tổng số bản ghi
            long countByUser = friendshipRepository.countByStatusAndUser(fs, user);
            long countByFriend = friendshipRepository.countByStatusAndFriend(fs, user);
            long totalElements = countByUser + countByFriend;

            // Chuyển đổi sang UserResponse
            List<UserResponse> friendResponses = friends.stream()
                    .map(f -> {
                        User friendUser = f.getUser().getId().equals(user.getId()) ? f.getFriend() : f.getUser();
                        return UserMapper.INSTANCE.toUserResponse(friendUser);
                    })
                    .toList();

            return new PageImpl<>(friendResponses, pageable, totalElements);
        } catch (IllegalArgumentException e) {
            log.error("Invalid friendship status: {}", friendStatusRequest);
            throw new AppException(ErrorCode.FRIENDSHIP_STATUS_NOT_VALID);
        }
    }
    public List<String> getAllYourFriendIds() {
        User user = commonService.getOrcreateInstanceAuthUser();
        return friendshipRepository.findAllFriendIds(user.getId());
    }
}