package com.ltev.connected.service.support;

import com.ltev.connected.domain.FriendRequest;
import com.ltev.connected.domain.RequestStatus;
import com.ltev.connected.domain.User;
import com.ltev.connected.dto.PostInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProfileInfo {

    private String loggedUsername;
    private User profileUser;
    private FriendRequest friendRequest;
    private List<PostInfo> postsInfo;

    public ProfileInfo(User profileUser) {
        this.profileUser = profileUser;
    }

    public RequestStatus getFriendRequestStatus() {
        return friendRequest == null
                ? RequestStatus.NOT_SENT
                : friendRequest.getStatus(loggedUsername);
    }

    /**
     *
     * @return true if logged user and profile user are friends, else false
     */
    public boolean isFriend() {
        return friendRequest != null && friendRequest.isAccepted();
    }
}