package com.ltev.connected.dao;

import com.ltev.connected.domain.Group;
import com.ltev.connected.domain.GroupRequest;
import com.ltev.connected.domain.User;
import com.ltev.connected.dto.GroupInfo;
import com.ltev.connected.dto.GroupManagerInfo;

import java.util.List;
import java.util.Optional;

public interface GroupDao {

    void save(Group group);

    void saveGroupAdmin(Long id, String username);

    List<Group> findGroupsByUsername(String username);

    Optional<GroupInfo> findGroupInfo(Long groupId, String loggedUsername);

    List<User> findAdmins(Long groupId);

    List<User> findMembers(Long groupId, int limit);

    void saveGroupRequest(GroupRequest groupRequest);

    List<GroupManagerInfo> findGroupsManagerInfoForAdmin(String adminName);
}