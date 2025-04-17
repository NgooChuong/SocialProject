package com.social.friendService.repository;

import com.social.friendService.entity.FriendShips;
import com.social.friendService.entity.User;
import com.social.friendService.enums.FriendshipStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendShipRepository extends JpaRepository<FriendShips, String> {
    Optional<FriendShips> findByUserAndFriend(User user, User friend);
    Optional<FriendShips> findByUser(User user);
    List<FriendShips> findFriendShipsByStatusAndUser(FriendshipStatus status, User user,Pageable pageable);
    List<FriendShips> findFriendShipsByStatusAndFriend(FriendshipStatus status, User friend,Pageable pageable);
    long countByStatusAndUser(FriendshipStatus status, User user);
    long countByStatusAndFriend(FriendshipStatus status, User user);
}
