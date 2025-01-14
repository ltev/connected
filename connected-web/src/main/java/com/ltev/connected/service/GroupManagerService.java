package com.ltev.connected.service;

import com.ltev.connected.domain.GroupRequest;
import com.ltev.connected.dto.GroupManagerInfo;

import java.util.List;

public interface GroupManagerService {

    List<GroupManagerInfo> findGroupsManagerInfoForAdmin();

    List<GroupRequest> getSentGroupRequests(Long groupId);

    void acceptGroupRequest(Long groupId, Long userId);

    void deleteGroup(Long valueOf);
}