package com.ltev.connected.service;

import com.ltev.connected.domain.FriendRequest;
import com.ltev.connected.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    Optional<User> findByUsername(String username);

    User findByUsernameJoinFetchPosts(String username);

    Optional<FriendRequest> getFriendRequest(User user);

    void sendFriendRequest(Long profileId);

    void acceptFriendRequest(Long profileId);

    List<User> findAllFriends();

    List<FriendRequest> findAllReceivedFriendshipRequests();

    void createNewUser(String username, String password);
}
