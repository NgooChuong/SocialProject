package com.social.notificationService.service;

import com.social.notificationService.configuration.TopicConfig;
import com.social.notificationService.dto.request.message.NotiFriendMessage;
import com.social.notificationService.dto.request.message.NotificationPostMessage;
import com.social.notificationService.dto.request.message.UserMessage;
import com.social.notificationService.dto.response.NotiResponse;
import com.social.notificationService.dto.response.PagedNotiResponse;
import com.social.notificationService.entity.Notification;
import com.social.notificationService.entity.NotificationFriend;
import com.social.notificationService.entity.NotificationPost;
import com.social.notificationService.entity.User;
import com.social.notificationService.enums.NotiEnum;
import com.social.notificationService.enums.NotiStatus;
import com.social.notificationService.exception.AppException;
import com.social.notificationService.exception.ErrorCode;
import com.social.notificationService.mapper.NotiMapper;
import com.social.notificationService.mapper.UserMapper;
import com.social.notificationService.repository.NotificationFriendRepository;
import com.social.notificationService.repository.NotificationPostRepository;
import com.social.notificationService.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;

import org.springframework.data.mongodb.core.aggregation.AggregationResults;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
@Slf4j
public class NotificationService {
    NotificationPostRepository notiPostRepository;
    NotificationFriendRepository notiFriendRepository;
    UserRepository userRepository;
    MongoTemplate mongoTemplate;
    TopicConfig topicConfig;

    private User store(User u) {
        return userRepository.save(u);
    }

    public PagedNotiResponse getNotifications(String receiverId, String type, int page, int size) {
        List<String> collectionNames = List.of("NotificationFriend", "NotificationPost");

        Aggregation aggregation = createAggregation(receiverId, collectionNames, type, page, size, false);
        Aggregation countAgg = createAggregation(receiverId, collectionNames, type, 0, 0, true);

        List<NotiResponse> notifications = mongoTemplate.aggregate(aggregation, collectionNames.getFirst(), Notification.class)
                .getMappedResults().stream()
                .map(pn -> NotiMapper.INSTANCE.toNotiResponse(pn, getSenderFromNoti(pn)))
                .toList();

        AggregationResults<Document> countResult = mongoTemplate.aggregate(countAgg, collectionNames.getFirst(), Document.class);
        long total = countResult.getUniqueMappedResult() != null
                ? countResult.getUniqueMappedResult().getInteger("count")
                : 0;

        return new PagedNotiResponse(notifications, page, size, total);
    }

    private Aggregation createAggregation(String receiverId, List<String> collectionNames, String type, int page, int size, boolean isCount) {
        List<AggregationOperation> operations = new ArrayList<>();

        // Lọc theo receiver.$id thay vì receiver._id
        Criteria matchCriteria = Criteria.where("receiver.$id").is(receiverId);
        if (!type.equals("ALL")) {
            try {
                NotiStatus notiStatus = NotiStatus.valueOf(type);
                matchCriteria.and("status").is(notiStatus);
            } catch (IllegalArgumentException e) {
                throw new AppException(ErrorCode.INVALID_TYPE);
            }
        }
        operations.add(Aggregation.match(matchCriteria));

        // Gộp các collection còn lại bằng $unionWith
        for (int i = 1; i < collectionNames.size(); i++) {
            String unionWithStage = type.equals("ALL")
                    ? "{$unionWith: { coll: '" + collectionNames.get(i) + "', pipeline: [{ $match: { 'receiver.$id': '" + receiverId + "' } }] }}"
                    : "{$unionWith: { coll: '" + collectionNames.get(i) + "', pipeline: [{ $match: { 'receiver.$id': '" + receiverId + "', 'status': '" + NotiStatus.valueOf(type) + "' } }] }}";
            operations.add(Aggregation.stage(unionWithStage));
        }

        // Thêm các bước sắp xếp và phân trang (nếu không phải đếm)
        if (!isCount) {
            operations.add(Aggregation.sort(Sort.by(Sort.Direction.DESC, "createdAt")));
            operations.add(Aggregation.skip((long) page * size));
            operations.add(Aggregation.limit(size));
        } else {
            operations.add(Aggregation.count().as("count"));
        }

        return Aggregation.newAggregation(operations);
    }

    private UserMessage getSenderFromNoti(Notification noti) {
        return switch (noti) {
            case NotificationFriend friend -> UserMapper.INSTANCE.toUserMessage(friend.getSender());
            case NotificationPost post -> UserMapper.INSTANCE.toUserMessage(post.getSender());
            default -> null;
        };
    }

    public Map<String, User> getFriendUsers(NotiFriendMessage note) {
        Map<String, User> userMap = new HashMap<>();
        User sender = store(UserMapper.INSTANCE.toUser(note.getSender()));
        User receiver = store(UserMapper.INSTANCE.toUser(note.getReceiver()));
        userMap.put("sender", sender);
        userMap.put("receiver", receiver);
        return userMap;
    }

    public Map<String, User> getPostUsers(NotificationPostMessage note) {
        Map<String, User> userMap = new HashMap<>();
        User sender = store(UserMapper.INSTANCE.toUser(note.getSender()));
        User receiver = store(UserMapper.INSTANCE.toUser(note.getReceiver()));
        userMap.put("sender", sender);
        userMap.put("receiver", receiver);
        return userMap;
    }

    public void store(String topic, NotiFriendMessage noti) {
        Map<String, User> userMap = getFriendUsers(noti);
        NotificationFriend friendNoti = NotificationFriend.builder()
                .createdAt(new Date(noti.getCreatedAt().getTime()))
                .sender(userMap.get("sender"))
                .receiver(userMap.get("receiver"))
                .status(NotiStatus.NON_VIEWED)
                .build();
        if (topic.equals(topicConfig.getFriend_topic_add())){
            friendNoti.setTitle(NotiEnum.FriendEnum.getTitle());
            friendNoti.setContent(NotiEnum.FriendEnum.getContent());
        }
        else if (topic.equals(topicConfig.getFriend_topic_acp())){
            friendNoti.setTitle(NotiEnum.FriendAcceptedEnum.getTitle());
            friendNoti.setContent(NotiEnum.FriendAcceptedEnum.getContent());
        }
        notiFriendRepository.save(friendNoti);
    }

    public void store(NotificationPostMessage noti) {
        Map<String, User> userMap = getPostUsers(noti);
        NotificationPost friendNoti = NotificationPost.builder()
                .title(NotiEnum.PostEnum.getTitle())
                .content(NotiEnum.PostEnum.getContent())
                .createdAt(new Date(noti.getCreatedAt().getTime()))
                .sender(userMap.get("sender"))
                .receiver(userMap.get("receiver"))
                .postId(noti.getPostId())
                .status(NotiStatus.NON_VIEWED)
                .build();
        notiPostRepository.save(friendNoti);
    }

    public Notification readNotification(String notificationId) {
        String userId = CommonService.getUserIdFromJwt();

        return updateStatusIfReceiverMatches(notiFriendRepository.findById(notificationId), userId)
                .orElseGet(() ->
                        updateStatusIfReceiverMatches(notiPostRepository.findById(notificationId), userId)
                                .orElseThrow(() -> new AppException(ErrorCode.VIEW_STATUS_ERROR))
                );
    }

    private Optional<Notification> updateStatusIfReceiverMatches(Optional<? extends Notification> optional, String userId) {
        return optional.filter(noti -> {
                    if (noti instanceof NotificationFriend friendNoti) {
                        return friendNoti.getReceiver() != null && userId.equals(friendNoti.getReceiver().getId());
                    } else if (noti instanceof NotificationPost postNoti) {
                        return postNoti.getReceiver() != null && userId.equals(postNoti.getReceiver().getId());
                    }
                    return false;
                })
                .map(noti -> {
                    noti.setStatus(NotiStatus.VIEWED);
                    if (noti instanceof NotificationFriend friendNoti) {
                        return notiFriendRepository.save(friendNoti);
                    } else {
                        NotificationPost postNoti = (NotificationPost) noti;
                        return notiPostRepository.save(postNoti);
                    }
                });
    }



}


