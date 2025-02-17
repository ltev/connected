package com.ltev.connected.service;

import com.ltev.connected.domain.FriendRequest;
import com.ltev.connected.domain.RequestStatus;
import com.ltev.connected.domain.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface FriendRequestService {

    void save(FriendRequest friendRequest);

    Optional<FriendRequest> getFriendRequest(User user1, User user2);

    void acceptFriendRequest(Long requestId, String loggedUser);

    List<FriendRequest> findAllByToUserNotAccepted(User user);

    List<FriendRequest> findAllByFromUserNotAccepted(User user);

    Map<RequestStatus, List<FriendRequest>> findAllReceivedAndSentNotAccepted(User user);

    boolean areFriends(User user1, User user2);
}