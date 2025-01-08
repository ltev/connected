package com.ltev.connected.repository;

import com.ltev.connected.domain.FriendRequest;
import com.ltev.connected.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    Optional<FriendRequest> findByFromUserAndToUser(User loggedUser, User user);

    @Transactional
    @Modifying
    @Query("update FriendRequest r set r.accepted = current_date " +
            "where r.accepted is null and r.id = ?1 and r.toUser.username = ?2")
    void acceptFriendRequestByIdAndUsername(Long requestId, String username);

    List<FriendRequest> findByToUserAndAccepted(User toUsers, LocalDate accepted);

    List<FriendRequest> findByFromUserAndAccepted(User fromUser, LocalDate accepted);
}
