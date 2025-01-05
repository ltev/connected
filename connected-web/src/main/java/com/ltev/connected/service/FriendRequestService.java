package com.ltev.connected.service;

import com.ltev.connected.domain.FriendRequest;
import com.ltev.connected.domain.User;

import java.util.List;
import java.util.Optional;

public interface FriendRequestService {

    void save(FriendRequest friendRequest);

    Optional<FriendRequest> getFriendRequest(User user1, User user2);

    void acceptFriendRequest(Long requestId);

    List<FriendRequest> findAllByToUserAndAccepted(User user);
}