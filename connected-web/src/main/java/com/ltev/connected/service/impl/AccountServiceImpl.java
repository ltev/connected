package com.ltev.connected.service.impl;

import com.ltev.connected.domain.GroupRequest;
import com.ltev.connected.domain.User;
import com.ltev.connected.domain.UserDetails;
import com.ltev.connected.repository.main.*;
import com.ltev.connected.repository.userData.UserDetailsRepository;
import com.ltev.connected.service.AccountService;
import com.ltev.connected.utils.AuthenticationUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final UserRepository userRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final ProfileSettingsRepository profileSettingsRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final GroupRequestRepository groupRequestRepository;

    @Transactional
    @Override
    public String deleteAccount() {
        Authentication authentication = AuthenticationUtils.checkAuthenticationOrThrow();

        User user = userRepository.findByUsername(authentication.getName()).get();
        user.setProfileSettings(null);
        Long userId = user.getId();

        // TODO: if user is an admin of a group that has only one admin force user to delete the group or
        // force it to give admin privileges to another user

        List<GroupRequest> groupRequests = groupRequestRepository.findByIdUserIdAndIsAdmin(userId, (byte) 1);
        if (! groupRequests.isEmpty()) {
            return String.format("You are currently admin of %d groups. Delete them first or give admin privileges " +
                    "to another member.", groupRequests.size());
        }

        // delete all user's likes
        likeRepository.deleteByIdUserId(userId);

        // delete all user's comments
        // in later versions there might be comments and likes on comments so they will have to be removed first
        commentRepository.deleteByUserId(userId);

        // delete all likes in user's posts
        likeRepository.deleteByIdPostUserId(userId);

        // delete all comments in user's posts
        commentRepository.deleteByPostUserId(userId);

        // delete all user's posts
        postRepository.deleteByUserId(userId);

        // delete all group request
        groupRequestRepository.deleteByIdUserId(userId);

        // delete all friend request
        friendRequestRepository.deleteByFromUserIdOrToUserId(userId, userId);

        // delete user data
        userDetailsRepository.deleteById(user.getId());

        // delete user, and cascade / orphan removal for profileSettings
        userRepository.delete(user);

        authentication.setAuthenticated(false);
        return null;
    }

    // TODO: assert later that details saved only if currently logged user is the same user that open the user-info-form
    @Override
    public void saveUserInfo(UserDetails userDetails) {
        AuthenticationUtils.checkAuthenticationOrThrow();

        if (userDetails.getId() == null) {
            User user = userRepository.findByUsername(AuthenticationUtils.getUsername()).get();
            // userDetails.setUser(user);
            userDetails.setId(user.getId());
        }
        userDetailsRepository.save(userDetails);
    }

    @Override
    public Optional<UserDetails> findUserInfo() {
        String username = AuthenticationUtils.checkAuthenticationOrThrow().getName();

        return userDetailsRepository.findById(userRepository.findByUsername(username).get().getId());
    }
}