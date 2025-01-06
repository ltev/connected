package com.ltev.connected.service.impl;

import com.ltev.connected.domain.FriendRequest;
import com.ltev.connected.domain.User;
import com.ltev.connected.repository.FriendRequestRepository;
import com.ltev.connected.service.FriendRequestService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
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
    public void acceptFriendRequest(Long requestId) {
        friendRequestRepository.acceptFriendRequest(requestId);
    }

    @Override
    public List<FriendRequest> findAllByToUserAndAccepted(User user) {
        return friendRequestRepository.findByToUserAndAccepted(user, null);
    }
}