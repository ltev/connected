package com.ltev.connected.service;

import com.ltev.connected.domain.Group;
import com.ltev.connected.dto.GroupInfo;
import com.ltev.connected.service.support.GroupsRequestInfo;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface GroupService {

    void createGroup(Group group);

    List<Group> getUserGroups();

    Optional<GroupInfo> getGroupInfo(Long groupId, Pageable pageable);

    void sendGroupRequest(Long groupId);

    boolean isAdminInAnyGroup();

    GroupsRequestInfo getGroupsRequest();

    void leaveGroup(Long groupId);

    Optional<GroupInfo> getGroupInfoWithMembers(Long groupId);
}