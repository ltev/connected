package com.ltev.connected.repository;

import com.ltev.connected.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    @Query("from User u where u.username = ?1")
    Optional<User> findByUsernameJoinFetchPosts(String username);

    @Query("select u.id from User u where u.username = ?1")
    Optional<Long> findIdByUsername(String username);
}
