package com.ltev.connected.service;

import com.ltev.connected.controller.support.SearchInfo;
import com.ltev.connected.domain.FriendRequest;
import com.ltev.connected.domain.RequestStatus;
import com.ltev.connected.domain.User;
import com.ltev.connected.domain.UserDetails;
import com.ltev.connected.service.support.ProfileInfo;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserService {

    Optional<User> findByUsername(String username);

    User findByUsernameJoinFetchPosts(String username);

    Optional<FriendRequest> getFriendRequest(User user);

    void sendFriendRequest(Long profileId);

    void acceptFriendRequest(Long profileId);

    List<User> findAllFriends();

    List<FriendRequest> findAllReceivedNotAcceptedFriendshipRequests();

    List<FriendRequest> findAllSentNotAcceptedFriendshipRequests();

    Map<RequestStatus, List<FriendRequest>> findAllReceivedAndSentNotAcceptedFriendshipRequests();

    void createNewUser(String username, String password);

    Optional<ProfileInfo> getInformationForShowingProfile(String username);

    List<UserDetails> searchForPeople(SearchInfo searchInfo);
}