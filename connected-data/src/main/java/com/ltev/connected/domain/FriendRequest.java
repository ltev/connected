package com.ltev.connected.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "friend_requests")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class FriendRequest implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "from_user_id")
    private User fromUser;

    @ManyToOne
    @JoinColumn(name = "to_user_id")
    private User toUser;

    public FriendRequest(User fromUser, User toUser) {
        this.fromUser = fromUser;
        this.toUser = toUser;
    }

    @CreationTimestamp(source = SourceType.DB)
    private LocalDate sent;

    private LocalDate accepted;

    public RequestStatus getStatus(String checkingUsername) {
        Long userId = null;
        if (fromUser.getUsername().equals(checkingUsername)) {
            userId = fromUser.getId();
        } else if (toUser.getUsername().equals(checkingUsername)) {
            userId = toUser.getId();
        }
        return getStatus(userId);
    }

    public RequestStatus getStatus(Long checkingUserId) {
        if (checkingUserId == null || (checkingUserId != fromUser.getId() && checkingUserId != toUser.getId())) {
            throw new RuntimeException("Invalid checkingUserId : " + checkingUserId);
        }
        RequestStatus status = getStatus();
        if (status == RequestStatus.SENT && checkingUserId == toUser.getId()) {
            status = RequestStatus.RECEIVED;
        }
        return status;
    }

    public boolean isAccepted() {
        return getStatus() == RequestStatus.ACCEPTED;
    }

    // == PRIVATE HELPER METHODS ==

    private RequestStatus getStatus() {
        return sent != null && accepted != null
                ? RequestStatus.ACCEPTED
                : sent != null
                    ? RequestStatus.SENT
                    : RequestStatus.NOT_SENT;
    }
}