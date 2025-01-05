package com.ltev.connected.service.impl;

import com.ltev.connected.dao.PostDao;
import com.ltev.connected.dao.UserDao;
import com.ltev.connected.domain.FriendRequest;
import com.ltev.connected.domain.Post;
import com.ltev.connected.domain.User;
import com.ltev.connected.service.FriendRequestService;
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
    private PostDao postDao;

    private FriendRequestService friendRequestService;

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
        return friendRequestService.getFriendRequest(loggedUser, user);
    }

    @Override
    public void sendFriendRequest(Long profileId) {
        Authentication authentication = AuthenticationUtils.checkAuthenticationOrThrow();

        User loggedUser = userDao.findByUsername(authentication.getName()).get();
        User user = userDao.findById(profileId).get();
        FriendRequest fr = new FriendRequest(loggedUser, user);
        friendRequestService.save(fr);
    }

    @Override
    public void acceptFriendRequest(Long requestId) {
        AuthenticationUtils.checkAuthenticationOrThrow();

        friendRequestService.acceptFriendRequest(requestId);
    }

    @Override
    public List<User> findAllFriends() {
        Authentication authentication = AuthenticationUtils.checkAuthenticationOrThrow();

        Long userId = userDao.findIdByUsername(authentication.getName()).get();
        return userDao.findAllFriends(userId);
    }

    @Override
    public List<FriendRequest> findAllReceivedNotAcceptedFriendshipRequests() {
        Authentication authentication = AuthenticationUtils.checkAuthenticationOrThrow();

        User user = userDao.findByUsername(authentication.getName()).get();
        return friendRequestService.findAllByToUserAndAccepted(user);
    }

    @Override
    public void createNewUser(String username, String password) {
        userDao.createNewUser(username, password);
    }

    public Optional<ProfileInfo> getInformationForShowingProfile(String profileUsername) {

        // check if profileUser exists
        Optional<User> profileUser = userDao.findByUsername(profileUsername);
        if (profileUser.isEmpty()) {
            return Optional.empty();
        }

        ProfileInfo profileInfo = new ProfileInfo(profileUser.get());

        if (AuthenticationUtils.isAuthenticated()) {
            User loggedUser = userDao.findByUsername(AuthenticationUtils.getAuthentication().getName()).get();
            profileInfo.setLoggedUsername(loggedUser.getUsername());

            // check for friend request and status
            Optional<FriendRequest> friendRequest = friendRequestService.getFriendRequest(loggedUser, profileUser.get());
            friendRequest.ifPresent(profileInfo::setFriendRequest);
        } else {
            List<Post> postsForEveryone = postDao.findPosts(profileUser.get().getId(), Post.Visibility.EVERYONE);
            profileInfo.setOpenPosts(postsForEveryone);
        }
        return Optional.of(profileInfo);
    }
}