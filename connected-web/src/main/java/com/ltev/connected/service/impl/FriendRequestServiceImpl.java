package com.ltev.connected.service.impl;

import com.ltev.connected.domain.FriendRequest;
import com.ltev.connected.domain.RequestStatus;
import com.ltev.connected.domain.User;
import com.ltev.connected.repository.main.FriendRequestRepository;
import com.ltev.connected.service.FriendRequestService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class FriendRequestServiceImpl implements FriendRequestService {

    private FriendRequestRepository friendRequestRepository;


    @Override
    public void save(FriendRequest friendRequest) {
        friendRequestRepository.save(friendRequest);
    }

    @Override
    public Optional<FriendRequest> getFriendRequest(User user1, User user2) {
        Optional<FriendRequest> foundRequest = friendRequestRepository.findByFromUserAndToUser(user1, user2);

        // found or change users order and try again
        if (foundRequest.isPresent()) {
            return foundRequest;
        } else {
            return friendRequestRepository.findByFromUserAndToUser(user2, user1);
        }
    }

    @Override
    public void acceptFriendRequest(Long requestId, String loggedUserName) {
        friendRequestRepository.acceptFriendRequestByIdAndUsername(requestId, loggedUserName);
    }

    @Override
    public List<FriendRequest> findAllByToUserNotAccepted(User user) {
        return friendRequestRepository.findByToUserAndAccepted(user, null);
    }

    @Override
    public List<FriendRequest> findAllByFromUserNotAccepted(User user) {
        return friendRequestRepository.findByFromUserAndAccepted(user, null);
    }

    @Override
    public Map<RequestStatus, List<FriendRequest>> findAllReceivedAndSentNotAccepted(User user) {
        Map<RequestStatus, List<FriendRequest>> map = new HashMap<>(2);
        map.put(RequestStatus.RECEIVED, findAllByToUserNotAccepted(user));
        map.put(RequestStatus.SENT, findAllByFromUserNotAccepted(user));
        return map;
    }

    @Override
    public boolean areFriends(User user1, User user2) {
        Optional<FriendRequest> friendRequest = getFriendRequest(user1, user2);
        return friendRequest.isPresent() && friendRequest.get().isAccepted();
    }
}