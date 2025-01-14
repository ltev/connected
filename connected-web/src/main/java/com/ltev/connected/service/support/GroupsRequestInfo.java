package com.ltev.connected.service.support;

import com.ltev.connected.domain.GroupRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * GroupRequests information for one user
 */
@Getter
@Setter
@ToString
public class GroupsRequestInfo {

    private List<GroupRequest> requests;
    private int numGroups;
    private int numAwaiting;
    private boolean isAdmin;

    public GroupsRequestInfo(List<GroupRequest> groupRequests) {
        if (groupRequests == null) {
            throw new IllegalArgumentException("Can not be null.");
        }
        this.requests = groupRequests;
        for (var groupRequest : groupRequests) {
            if (groupRequest.isMember()) {
                numGroups++;
                if (groupRequest.isAdmin()) {
                    isAdmin = true;
                }
            } else {
                numAwaiting++;
            }
        }
    }
}