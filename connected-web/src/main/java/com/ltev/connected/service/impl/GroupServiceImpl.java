package com.ltev.connected.service.impl;

import com.ltev.connected.dao.GroupDao;
import com.ltev.connected.dao.PostDao;
import com.ltev.connected.dao.UserDao;
import com.ltev.connected.domain.Group;
import com.ltev.connected.domain.GroupRequest;
import com.ltev.connected.domain.User;
import com.ltev.connected.dto.GroupInfo;
import com.ltev.connected.dto.PostInfo;
import com.ltev.connected.exception.AccessDeniedException;
import com.ltev.connected.exception.PageOutOfBoundsException;
import com.ltev.connected.repository.GroupRequestRepository;
import com.ltev.connected.service.GroupService;
import com.ltev.connected.service.support.GroupsRequestInfo;
import com.ltev.connected.utils.AuthenticationUtils;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupDao groupDao;
    private final UserDao userDao;
    private final PostDao postDao;
    private final GroupRequestRepository groupRequestRepository;

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
    public Optional<GroupInfo> getGroupInfo(Long groupId, Pageable pageable) {
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

            // get list of postInfo
            Page<PostInfo> postInfoPage = postDao.findGroupPostsInfo(
                    groupId, groupInfo.getGroupRequest().getId().getUser().getId(), pageable);

            // page number too outside range
            if (! postInfoPage.isFirst() && postInfoPage.getTotalElements() == 0) {
                throw new PageOutOfBoundsException("Out of range page: " + pageable.getPageNumber());
//                  read first page
//                pageable = pageable.withPage(0);
//                postInfoPage = postDao.findGroupPostsInfo(
//                        groupId, groupInfo.getGroupRequest().getId().getUser().getId(), pageable);
            }
            groupInfo.setPostInfoPage(postInfoPage);
        }
        return groupInfoOptional;
    }

    @Override
    public void sendGroupRequest(Long groupId) {
        Authentication authentication = AuthenticationUtils.checkAuthenticationOrThrow();

        User loggedUser = userDao.findByUsername(authentication.getName()).get();
        groupDao.saveGroupRequest(new GroupRequest(new GroupRequest.Id(new Group(groupId), loggedUser)));
    }

    @Override
    public boolean isAdminInAnyGroup() {
        String username = AuthenticationUtils.checkAuthenticationOrThrow().getName();

        return groupDao.findCountByUsernameAndIsAdmin(username) > 0;
    }

    /**
     * @return info with all send and accepted groups requests for logged user
     */
    @Override
    public GroupsRequestInfo getGroupsRequest() {
        String username = AuthenticationUtils.checkAuthenticationOrThrow().getName();

        List<GroupRequest> groupsRequest = groupDao.findGroupRequestsByUsername(username);
        return new GroupsRequestInfo(groupsRequest);
    }

    @Override
    public void leaveGroup(Long groupId) {
        String username = AuthenticationUtils.checkAuthenticationOrThrow().getName();

        groupDao.deleteGroupRequest(groupId, username);
    }

    /**
     * @return groupInfo with members (id, username) when logged users is a member of that group
     */
    @Override
    public Optional<GroupInfo> getGroupInfoWithMembers(Long groupId) {
        assertLoggedUserIsGroupMember(groupId);

        return groupDao.findGroupInfoWithMembers(groupId);
    }

    // == PRIVATE HELPER METHODS ==

    private void assertLoggedUserIsGroupMember(Long groupId) {
        String username = AuthenticationUtils.checkAuthenticationOrThrow().getName();

        Optional<GroupRequest> groupRequestOptional =
                groupRequestRepository.findByIdGroupIdAndIdUserUsername(groupId, username);
        if (groupRequestOptional.isEmpty() || !groupRequestOptional.get().isMember()) {
            throw new AccessDeniedException("Not a group member.");
        }
    }
}