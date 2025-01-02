package com.ltev.connected.service.impl;

import com.ltev.connected.domain.FriendRequest;
import com.ltev.connected.domain.User;
import com.ltev.connected.repository.FriendRequestRepository;
import com.ltev.connected.repository.UserRepository;
import com.ltev.connected.service.UserService;
import com.ltev.connected.utils.AuthenticationUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private FriendRequestRepository friendRequestRepository;

    @Override
    public User findByUsernameJoinFetchPosts(String username) {
        return userRepository.findByUsernameJoinFetchPosts(username)
                .orElseThrow(() -> new NoSuchElementException("No user with username: " + username));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<FriendRequest> getFriendRequest(User user) {
        Authentication authentication = AuthenticationUtils.checkAuthenticationOrThrow();

        User loggedUser = userRepository.findByUsername(authentication.getName()).get();
        Optional<FriendRequest> foundRequest = friendRequestRepository.findByFromUserAndToUser(loggedUser, user);

        // found or change users order and try again
        if (foundRequest.isPresent()) {
            return foundRequest;
        } else {
            return friendRequestRepository.findByFromUserAndToUser(user, loggedUser);
        }
    }

    @Override
    public void sendFriendRequest(Long profileId) {
        Authentication authentication = AuthenticationUtils.checkAuthenticationOrThrow();

        User loggedUser = userRepository.findByUsername(authentication.getName()).get();
        User user = userRepository.findById(profileId).get();
        FriendRequest fr = new FriendRequest(loggedUser, user);
        friendRequestRepository.save(fr);
    }

    @Override
    public void acceptFriendRequest(Long requestId) {
        Authentication authentication = AuthenticationUtils.checkAuthenticationOrThrow();

        friendRequestRepository.acceptFriendRequest(requestId);
    }
}
