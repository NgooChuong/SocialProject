package com.social.notificationService.repository;

import com.social.notificationService.entity.NotificationFriend;
import com.social.notificationService.entity.User;
import com.social.notificationService.enums.NotiStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationFriendRepository extends MongoRepository<NotificationFriend, String> {
    List<NotificationFriend> findByReceiver(User receiver);
    Page<NotificationFriend> findByReceiverAndStatus(User receiver, NotiStatus status, Pageable pageable);

}
