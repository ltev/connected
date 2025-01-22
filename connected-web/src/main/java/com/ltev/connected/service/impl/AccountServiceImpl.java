package com.ltev.connected.service.impl;

import com.ltev.connected.dao.UserDao;
import com.ltev.connected.domain.User;
import com.ltev.connected.domain.UserDetails;
import com.ltev.connected.repository.userData.UserDetailsRepository;
import com.ltev.connected.service.AccountService;
import com.ltev.connected.utils.AuthenticationUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {

    private UserDetailsRepository userDetailsRepository;
    private UserDao userDao;

    @Override
    public void deleteAccount() {
        Authentication authentication = AuthenticationUtils.checkAuthenticationOrThrow();
        userDao.deleteUser(authentication.getName());
        authentication.setAuthenticated(false);
    }

    // TODO: assert later that details saved only if currently logged user is the same user that open the user-info-form
    @Override
    public void saveUserInfo(UserDetails userDetails) {
        AuthenticationUtils.checkAuthenticationOrThrow();

        if (userDetails.getId() == null) {
            User user = userDao.findByUsername(AuthenticationUtils.getUsername()).get();
            userDetails.setUser(user);
        }
        userDetailsRepository.save(userDetails);
    }

    @Override
    public Optional<UserDetails> findUserInfo() {
        AuthenticationUtils.checkAuthenticationOrThrow();

        return userDetailsRepository.findByUserUsername(AuthenticationUtils.getUsername());
    }
}