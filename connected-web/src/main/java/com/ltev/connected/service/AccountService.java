package com.ltev.connected.service;

import com.ltev.connected.domain.UserDetails;

import java.util.Optional;

public interface AccountService {

    void deleteAccount();

    void saveUserInfo(UserDetails userDetails);

    Optional<UserDetails> findUserInfo();
}