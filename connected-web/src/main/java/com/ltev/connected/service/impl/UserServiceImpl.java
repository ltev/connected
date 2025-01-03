package com.ltev.connected.service.impl;

import com.ltev.connected.dao.UserDao;
import com.ltev.connected.domain.FriendRequest;
import com.ltev.connected.domain.User;
import com.ltev.connected.repository.FriendRequestRepository;
import com.ltev.connected.service.UserService;
import com.ltev.connected.utils.AuthenticationUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserDao userDao;
    private FriendRequestRepository friendRequestRepository;

    @Override
    public User findByUsernameJoinFetchPosts(String username) {
        return userDao.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("No user with username: " + username));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Override
    public Optional<FriendRequest> getFriendRequest(User user) {
        Authentication authentication = AuthenticationUtils.checkAuthenticationOrThrow();

        User loggedUser = userDao.findByUsername(authentication.getName()).get();
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

        User loggedUser = userDao.findByUsername(authentication.getName()).get();
        User user = userDao.findById(profileId).get();
        FriendRequest fr = new FriendRequest(loggedUser, user);
        friendRequestRepository.save(fr);
    }

    @Override
    public void acceptFriendRequest(Long requestId) {
        AuthenticationUtils.checkAuthenticationOrThrow();

        friendRequestRepository.acceptFriendRequest(requestId);
    }

    @Override
    public List<User> findAllFriends() {
        Authentication authentication = AuthenticationUtils.checkAuthenticationOrThrow();

        Long userId = userDao.findIdByUsername(authentication.getName()).get();

        return userDao.findAllFriends(userId);
    }
}
