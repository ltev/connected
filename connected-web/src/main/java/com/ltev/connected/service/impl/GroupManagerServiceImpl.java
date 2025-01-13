package com.ltev.connected.service.impl;

import com.ltev.connected.dao.GroupDao;
import com.ltev.connected.domain.Group;
import com.ltev.connected.domain.GroupRequest;
import com.ltev.connected.dto.GroupManagerInfo;
import com.ltev.connected.repository.GroupRequestRepository;
import com.ltev.connected.service.GroupManagerService;
import com.ltev.connected.utils.AuthenticationUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class GroupManagerServiceImpl implements GroupManagerService {

    private final GroupDao groupDao;
    private final GroupRequestRepository groupRequestRepository;

    @Override
    public List<GroupManagerInfo> findGroupsManagerInfoForAdmin() {
        String loggedUser = AuthenticationUtils.checkAuthenticationOrThrow().getName();

        return groupDao.findGroupsManagerInfoForAdmin(loggedUser);
    }

    /**
     * @param groupId
     * @return List<GroupRequest> - sent but not accepted yet
     */
    @Override
    public List<GroupRequest> getSentGroupRequests(Long groupId) {
        return groupRequestRepository.findByIdGroupAndAccepted(new Group(groupId), null);
    }

    @Override
    public void acceptGroupRequest(Long groupId, Long userId) {
        groupRequestRepository.acceptGroupRequest(groupId, userId);
    }
}