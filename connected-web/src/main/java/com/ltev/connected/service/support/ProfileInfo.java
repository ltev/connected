package com.ltev.connected.service.support;

import com.ltev.connected.domain.FriendRequest;
import com.ltev.connected.domain.Post;
import com.ltev.connected.domain.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProfileInfo {

    private String loggedUsername;
    private User profileUser;
    private FriendRequest friendRequest;

    /**
     * ProfileUser posts with Visibility = EVERYONE
     */
    private List<Post> openPosts;

    public ProfileInfo(User profileUser) {
        this.profileUser = profileUser;
    }

    public FriendRequest.Status getFriendRequestStatus() {
        return friendRequest == null
                ? FriendRequest.Status.NOT_SENT
                : friendRequest.getStatus(loggedUsername);
    }

}
