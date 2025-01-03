package com.ltev.connected.service.impl;

import com.ltev.connected.dao.UserDao;
import com.ltev.connected.domain.FriendRequest;
import com.ltev.connected.domain.User;
import com.ltev.connected.repository.FriendRequestRepository;
import com.ltev.connected.repository.UserRepository;
import com.ltev.connected.service.UserService;
import com.ltev.connected.service.support.ProfileInfo;
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
    private final UserRepository userRepository;

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
        return getFriendRequest(loggedUser, user);
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

    @Override
    public List<FriendRequest> findAllReceivedFriendshipRequests() {
        Authentication authentication = AuthenticationUtils.checkAuthenticationOrThrow();

        User user = userDao.findByUsername(authentication.getName()).get();
        return friendRequestRepository.findAllByToUser(user);
    }

    @Override
    public void createNewUser(String username, String password) {
        userDao.createNewUser(username, password);
    }

    public Optional<ProfileInfo> getInformationForShowingProfile(String profileUsername) {

        // check if profileUser exists
        Optional<User> profileUser = userRepository.findByUsername(profileUsername);
        if (profileUser.isEmpty()) {
            return Optional.empty();
        }

        ProfileInfo profileInfo = new ProfileInfo(profileUser.get());

        if (AuthenticationUtils.isAuthenticated()) {
            User loggedUser = userDao.findByUsername(AuthenticationUtils.getAuthentication().getName()).get();
            profileInfo.setLoggedUsername(Optional.of(loggedUser.getUsername()));

            // check for friend request and status
            Optional<FriendRequest> friendRequest = getFriendRequest(loggedUser, profileUser.get());
            profileInfo.setFriendRequest(friendRequest);

        }
        return Optional.of(profileInfo);
    }

    // == PRIVATE HELPER METHODS ==

    public Optional<FriendRequest> getFriendRequest(User user1, User user2) {
        Optional<FriendRequest> foundRequest = friendRequestRepository.findByFromUserAndToUser(user1, user2);

        // found or change users order and try again
        if (foundRequest.isPresent()) {
            return foundRequest;
        } else {
            return friendRequestRepository.findByFromUserAndToUser(user2, user1);
        }
    }
}
