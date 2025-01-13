package com.ltev.connected.service.impl;

import com.ltev.connected.dao.GroupDao;
import com.ltev.connected.domain.Group;
import com.ltev.connected.domain.GroupRequest;
import com.ltev.connected.dto.GroupManagerInfo;
import com.ltev.connected.exception.AccessDeniedException;
import com.ltev.connected.repository.GroupRequestRepository;
import com.ltev.connected.service.GroupManagerService;
import com.ltev.connected.utils.AuthenticationUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
        String username = AuthenticationUtils.checkAuthenticationOrThrow().getName();

        Optional<GroupRequest> loggedUserRequest = groupRequestRepository.findByIdGroupIdAndIdUserUsername(groupId, username);
        if (loggedUserRequest.isEmpty() || loggedUserRequest.get().isAdmin() == false) {
            throw new AccessDeniedException("Logged user has no admin role for groupId: " + groupId);
        }
        return groupRequestRepository.findByIdGroupAndAccepted(new Group(groupId), null);
    }

    @Override
    public void acceptGroupRequest(Long groupId, Long userId) {
        groupRequestRepository.acceptGroupRequest(groupId, userId);
    }
}