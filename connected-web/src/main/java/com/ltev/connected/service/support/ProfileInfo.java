package com.ltev.connected.service.support;

import com.ltev.connected.domain.FriendRequest;
import com.ltev.connected.domain.User;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
public class ProfileInfo {

    private User profileUser;
    private Optional<String> loggedUsername;
    private Optional<FriendRequest> friendRequest;

    public ProfileInfo(User profileUser) {
        this.profileUser = profileUser;
        this.loggedUsername = Optional.empty();
        this.friendRequest = Optional.empty();
    }
}
