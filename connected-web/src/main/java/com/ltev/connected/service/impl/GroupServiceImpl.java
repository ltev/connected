package com.ltev.connected.service.impl;

import com.ltev.connected.dao.GroupDao;
import com.ltev.connected.dao.UserDao;
import com.ltev.connected.domain.Group;
import com.ltev.connected.domain.GroupRequest;
import com.ltev.connected.domain.User;
import com.ltev.connected.dto.GroupInfo;
import com.ltev.connected.service.GroupService;
import com.ltev.connected.utils.AuthenticationUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupDao groupDao;
    private final UserDao userDao;

    @Override
    public void createGroup(Group group) {
        groupDao.save(group);
        groupDao.saveGroupAdmin(group.getId(), AuthenticationUtils.getUsername());
    }

    @Override
    public List<Group> getUserGroups() {
        return groupDao.findGroupsByUsername(AuthenticationUtils.getUsername());
    }

    @Override
    public Optional<GroupInfo> getGroupInfo(Long groupId) {
        Optional<GroupInfo> groupInfoOptional = groupDao.findGroupInfo(groupId, AuthenticationUtils.getUsername());

        if (groupInfoOptional.isEmpty()) {
            return Optional.empty();
        }

        GroupInfo groupInfo = groupInfoOptional.get();

        // get admins
        groupInfo.getGroup().setAdmins(groupDao.findAdmins(groupId));

        // get information for a group member
        if (groupInfo.getGroupRequest().isMember()) {
            // get 4 members
            int limit = 4;
            groupInfo.getGroup().setMembers(groupDao.findMembers(groupId, limit));
        }
        return groupInfoOptional;
    }

    @Override
    public void sendGroupRequest(Long groupId) {
        Authentication authentication = AuthenticationUtils.checkAuthenticationOrThrow();

        User loggedUser = userDao.findByUsername(authentication.getName()).get();
        groupDao.saveGroupRequest(new GroupRequest(new GroupRequest.Id(new Group(groupId), loggedUser)));
    }
}