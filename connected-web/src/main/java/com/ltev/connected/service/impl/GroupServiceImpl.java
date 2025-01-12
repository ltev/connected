package com.ltev.connected.service.impl;

import com.ltev.connected.dao.GroupDao;
import com.ltev.connected.domain.Group;
import com.ltev.connected.dto.GroupInfo;
import com.ltev.connected.service.GroupService;
import com.ltev.connected.utils.AuthenticationUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class GroupServiceImpl implements GroupService {

    private GroupDao groupDao;

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

        // get information for a group member
        if (groupInfo.isMember()) {
            // get admins
            groupInfo.getGroup().setAdmins(groupDao.findAdmins(groupId));

            // get 4 members
            int limit = 4;
            groupInfo.getGroup().setMembers(groupDao.findMembers(groupId, limit));
        }
        return groupInfoOptional;
    }
}