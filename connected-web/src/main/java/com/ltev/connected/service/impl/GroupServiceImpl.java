package com.ltev.connected.service.impl;

import com.ltev.connected.dao.UserDao;
import com.ltev.connected.domain.Group;
import com.ltev.connected.domain.User;
import com.ltev.connected.repository.GroupRepository;
import com.ltev.connected.service.GroupService;
import com.ltev.connected.utils.AuthenticationUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class GroupServiceImpl implements GroupService {

    private GroupRepository groupRepository;
    private UserDao userDao;

    @Override
    public void createGroup(Group group) {
        String username = AuthenticationUtils.checkAuthenticationOrThrow().getName();

        User admin = userDao.findByUsername(username).get();
        group.setAdmins(List.of(admin));
        groupRepository.save(group);
    }
}