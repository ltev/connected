package com.ltev.connected.dao.impl;

import com.ltev.connected.dao.UserDao;
import com.ltev.connected.domain.User;
import com.ltev.connected.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@AllArgsConstructor
public class UserDaoImpl implements UserDao {

    private UserRepository repository;

    @Override
    public Optional<User> findByUsername(String username) {
        return repository.findByUsername(username);
    }

    @Override
    public Optional<Long> findIdByUsername(String username) {
        return repository.findIdByUsername(username);
    }

    @Override
    public Optional<User> findById(Long userId) {
        return repository.findById(userId);
    }
}
