package com.ltev.connected.service.support;

import com.ltev.connected.domain.FriendRequest;
import com.ltev.connected.domain.RequestStatus;
import com.ltev.connected.domain.User;
import com.ltev.connected.dto.ProfileInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class ProfileInfoTest {

    static User erik;
    static User dave;

    @BeforeEach
    void beforeAll() {
        erik = new User();
        erik.setId(1L);
        erik.setUsername("Erik");

        dave = new User();
        dave.setId(2L);
        dave.setUsername("Dave");
    }

    @Test
    void getFriendRequestStatus_notSent() {
        FriendRequest friendRequest = new FriendRequest();

        friendRequest.setFromUser(erik);
        friendRequest.setToUser(dave);

        ProfileInfo profileInfo = new ProfileInfo(dave);
        profileInfo.setLoggedUsername(erik.getUsername());
        profileInfo.setFriendRequest(friendRequest);

        assertThat(profileInfo.getFriendRequestStatus()).isEqualTo(RequestStatus.NOT_SENT);
    }

    @Test
    void getFriendRequestStatus_sent() {
        FriendRequest friendRequest = new FriendRequest();

        friendRequest.setFromUser(erik);
        friendRequest.setToUser(dave);
        friendRequest.setSent(LocalDate.now());

        ProfileInfo profileInfo = new ProfileInfo(dave);
        profileInfo.setLoggedUsername(erik.getUsername());
        profileInfo.setFriendRequest(friendRequest);

        assertThat(profileInfo.getFriendRequestStatus()).isEqualTo(RequestStatus.SENT);
    }

    @Test
    void getFriendRequestStatus_received() {
        FriendRequest friendRequest = new FriendRequest();

        friendRequest.setFromUser(erik);
        friendRequest.setToUser(dave);
        friendRequest.setSent(LocalDate.now());

        ProfileInfo profileInfo = new ProfileInfo(erik);
        profileInfo.setLoggedUsername(dave.getUsername());
        profileInfo.setFriendRequest(friendRequest);

        assertThat(profileInfo.getFriendRequestStatus()).isEqualTo(RequestStatus.RECEIVED);
    }

    @Test
    void getFriendRequestStatus_accepted1() {
        FriendRequest friendRequest = new FriendRequest();

        friendRequest.setFromUser(erik);
        friendRequest.setToUser(dave);
        friendRequest.setSent(LocalDate.now());
        friendRequest.setAccepted(LocalDate.now());

        ProfileInfo profileInfo = new ProfileInfo(dave);
        profileInfo.setLoggedUsername(erik.getUsername());
        profileInfo.setFriendRequest(friendRequest);

        assertThat(profileInfo.getFriendRequestStatus()).isEqualTo(RequestStatus.ACCEPTED);
    }

    @Test
    void getFriendRequestStatus_accepted2() {
        FriendRequest friendRequest = new FriendRequest();

        friendRequest.setFromUser(dave);
        friendRequest.setToUser(erik);
        friendRequest.setSent(LocalDate.now());
        friendRequest.setAccepted(LocalDate.now());

        ProfileInfo profileInfo = new ProfileInfo(dave);
        profileInfo.setLoggedUsername(erik.getUsername());
        profileInfo.setFriendRequest(friendRequest);

        assertThat(profileInfo.getFriendRequestStatus()).isEqualTo(RequestStatus.ACCEPTED);
    }
}