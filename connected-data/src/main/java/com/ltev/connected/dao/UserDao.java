package com.ltev.connected.dao;

import com.ltev.connected.domain.Post;
import com.ltev.connected.domain.User;
import com.ltev.connected.dto.ProfileInfo;

import java.util.List;
import java.util.Optional;

public interface UserDao {

    void deleteUser(String username);

    void createNewUser(String username, String password);

    void createNewUser(User user);

    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameAndVisibility(String username, List<Post.Visibility> visibilities);

    Optional<Long> findIdByUsername(String username);

    Optional<User> findById(Long userId);

    List<User> findAllFriends(Long userId);

    List<User> findCommonFriends(Long user1Id, Long user2Id);

    ProfileInfo updateNumFriendsAndNumGroups(ProfileInfo profileInfo, Long userId);
}
