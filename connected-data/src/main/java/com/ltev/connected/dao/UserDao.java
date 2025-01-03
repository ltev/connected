package com.ltev.connected.dao;

import com.ltev.connected.domain.User;

import java.util.Optional;

public interface UserDao {

    Optional<User> findByUsername(String username);

    Optional<Long> findIdByUsername(String username);

    Optional<User> findById(Long userId);
}
