package com.ltev.connected.domain;

import jakarta.persistence.*;
import lombok.*;
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

    public enum Status {
        NOT_SENT,
        SENT,
        RECEIVED,
        ACCEPTED
    }

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

    public Status getStatus(String checkingUsername) {
        Long userId = null;
        if (fromUser.getUsername().equals(checkingUsername)) {
            userId = fromUser.getId();
        } else if (toUser.getUsername().equals(checkingUsername)) {
            userId = toUser.getId();
        }
        return getStatus(userId);
    }

    public Status getStatus(Long checkingUserId) {
        if (checkingUserId == null || (checkingUserId != fromUser.getId() && checkingUserId != toUser.getId())) {
            throw new RuntimeException("Invalid checkingUserId : " + checkingUserId);
        }
        Status status = getStatus();
        if (status == Status.SENT && checkingUserId == toUser.getId()) {
            status = Status.RECEIVED;
        }
        return status;
    }

    public boolean isAccepted() {
        return getStatus() == Status.ACCEPTED;
    }

    // == PRIVATE HELPER METHODS ==

    private Status getStatus() {
        return sent != null && accepted != null
                ? Status.ACCEPTED
                : sent != null
                    ? Status.SENT
                    : Status.NOT_SENT;
    }
}