package com.ltev.connected.service;

import com.ltev.connected.dto.GroupInfo;
import com.ltev.connected.domain.Group;

import java.util.List;
import java.util.Optional;

public interface GroupService {

    void createGroup(Group group);

    List<Group> getUserGroups();

    Optional<GroupInfo> getGroupInfo(Long groupId);

    void sendGroupRequest(Long groupId);
}