package com.ltev.connected.service.impl;

import com.ltev.connected.dao.GroupDao;
import com.ltev.connected.dao.PostDao;
import com.ltev.connected.dao.UserDao;
import com.ltev.connected.dao.UserDetailsDao;
import com.ltev.connected.domain.*;
import com.ltev.connected.dto.PostInfo;
import com.ltev.connected.dto.ProfileInfo;
import com.ltev.connected.dto.SearchInfo;
import com.ltev.connected.repository.main.ProfileSettingsRepository;
import com.ltev.connected.service.FriendRequestService;
import com.ltev.connected.service.UserService;
import com.ltev.connected.utils.AuthenticationUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserDao userDao;
    private PostDao postDao;
    private GroupDao groupDao;
    private FriendRequestService friendRequestService;
    private UserDetailsDao userDetailsDao;

    private ProfileSettingsRepository profileSettingsRepository;

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

        friendRequestService.acceptFriendRequest(requestId, AuthenticationUtils.getUsername());
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
        return friendRequestService.findAllByToUserNotAccepted(user);
    }

    @Override
    public List<FriendRequest> findAllSentNotAcceptedFriendshipRequests() {
        Authentication authentication = AuthenticationUtils.checkAuthenticationOrThrow();

        User user = userDao.findByUsername(authentication.getName()).get();
        return friendRequestService.findAllByFromUserNotAccepted(user);
    }

    @Override
    public Map<RequestStatus, List<FriendRequest>> findAllReceivedAndSentNotAcceptedFriendshipRequests() {
        Authentication authentication = AuthenticationUtils.checkAuthenticationOrThrow();

        User user = userDao.findByUsername(authentication.getName()).get();
        return friendRequestService.findAllReceivedAndSentNotAccepted(user);
    }

    @Override
    public void createNewUser(String username, String password, BCryptPasswordEncoder encoder) {
        ProfileSettings profileSettings = new ProfileSettings();
        profileSettings.setFriendsVisibility(Visibility.PUBLIC);
        profileSettings.setGroupsVisibility(Visibility.PUBLIC);

        User user = new User();
        user.setUsername(username);
        user.setPassword("{bcrypt}" + encoder.encode(password));
        user.setEnabled((byte) 1);
        user.setProfileSettings(profileSettings);

        userDao.createNewUser(user);
    }

    @Override
    public Optional<ProfileInfo> getInformationForShowingProfile(String profileUsername) {
        Optional<ProfileSettings> profileSettingsOptional = profileSettingsRepository.findByUserUsername(profileUsername);

        if (profileSettingsOptional.isEmpty()) {
            return Optional.empty();
        }

        User profileUser = profileSettingsOptional.get().getUser();
        ProfileInfo profileInfo = new ProfileInfo(profileUser);

        List<Post.Visibility> visibilities = new ArrayList<>();
        visibilities.add(Post.Visibility.PUBLIC);

        // find posts
        List<PostInfo> postsInfo;

        // decide what visibility
        if (AuthenticationUtils.isAuthenticated()) {
            visibilities.add(Post.Visibility.LOGGED_USERS);

            User loggedUser = userDao.findByUsername(AuthenticationUtils.getAuthentication().getName()).get();
            profileInfo.setLoggedUsername(loggedUser.getUsername());

            // check for friend request and status
            Optional<FriendRequest> friendRequest = friendRequestService.getFriendRequest(loggedUser, profileUser);

            if (friendRequest.isPresent()) {
                profileInfo.setFriendRequest(friendRequest.get());

                if (friendRequest.get().isAccepted()) {
                    visibilities.add(Post.Visibility.FRIENDS);
                }
            }
            postsInfo = postDao.findPostsInfo(profileUsername, visibilities, loggedUser.getUsername());

            // get numFriends and numGroups
            userDao.updateNumFriendsAndNumGroups(profileInfo, profileUser.getId());

            // find common friends sample
            profileInfo.setCommonFriends(userDao.findCommonFriends(loggedUser.getId(), profileUser.getId()));

            // find groups
            profileInfo.setGroups(groupDao.findGroupsByUserId(profileUser.getId(), 2));
        } else {
            postsInfo = postDao.findPostsInfo(profileUsername, visibilities, null);
        }
        profileInfo.setPostsInfo(postsInfo);

        return Optional.of(profileInfo);
    }

    @Deprecated
    public Optional<ProfileInfo> getInformationForShowingProfile_Deprecated(String profileUsername) {

        // check if profileUser exists
        Optional<User> profileUser = userDao.findByUsername(profileUsername);
        if (profileUser.isEmpty()) {
            return Optional.empty();
        }

        ProfileInfo profileInfo = new ProfileInfo(profileUser.get());
        List<Post> foundPosts;

        if (AuthenticationUtils.isAuthenticated()) {
            List<Post.Visibility> acceptedVisibilities = new ArrayList<>();
            acceptedVisibilities.add(Post.Visibility.PUBLIC);
            acceptedVisibilities.add(Post.Visibility.LOGGED_USERS);

            User loggedUser = userDao.findByUsername(AuthenticationUtils.getAuthentication().getName()).get();
            profileInfo.setLoggedUsername(loggedUser.getUsername());

            // check for friend request and status
            Optional<FriendRequest> friendRequest = friendRequestService.getFriendRequest(loggedUser, profileUser.get());

            if (friendRequest.isPresent()) {
                profileInfo.setFriendRequest(friendRequest.get());

                if (friendRequest.get().isAccepted()) {
                    acceptedVisibilities.add(Post.Visibility.FRIENDS);
                }
            }
            foundPosts = postDao.findPosts(profileUser.get(), acceptedVisibilities);
            // User endProfileUser = userDao.findByUsernameAndVisibility(profileUsername, acceptedVisibilities).get();
            // profileInfo.setProfileUser(endProfileUser);
        } else {
            foundPosts = postDao.findPosts(profileUser.get().getId(), Post.Visibility.PUBLIC);
        }

        profileInfo.getProfileUser().setPosts(foundPosts);
        return Optional.of(profileInfo);
    }

    @Override
    public List<UserDetails> searchForPeople(SearchInfo searchInfo) {
        AuthenticationUtils.checkAuthenticationOrThrow();

        // Used before migrating UserDetails into different db
//        List<UserDetails> userDetails = switch (searchInfo.searchBy()) {
//            case "-firstName" -> userDetailsRepository.findByFirstName(searchInfo.getFirstName());
//            case "-lastName" -> userDetailsRepository.findByLastName(searchInfo.getLastName());
//            case "-age" -> userDetailsRepository.findByBirthdayBetween(
//                    searchInfo.fromDate(), searchInfo.toDate());
//            case "-firstName-lastName" -> userDetailsRepository.findByFirstNameAndLastName(
//                    searchInfo.getFirstName(), searchInfo.getLastName());
//            case "-firstName-age" -> userDetailsRepository.findByFirstNameAndBirthdayBetween(
//                    searchInfo.getFirstName(), searchInfo.fromDate(), searchInfo.toDate());
//            case "-lastName-age" -> userDetailsRepository.findByLastNameAndBirthdayBetween(
//                    searchInfo.getLastName(), searchInfo.fromDate(), searchInfo.toDate());
//            case "-firstName-lastName-age" -> userDetailsRepository.findByFirstNameAndLastNameAndBirthdayBetween(
//                    searchInfo.getFirstName(), searchInfo.getLastName(), searchInfo.fromDate(), searchInfo.toDate());
//            case "" -> Collections.emptyList();
//            default -> throw new RuntimeException("Not defined");
//        };
        return userDetailsDao.findBySearchInfo(searchInfo);
    }
}