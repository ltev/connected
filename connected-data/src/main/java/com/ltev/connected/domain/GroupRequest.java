package com.ltev.connected.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;

import java.time.LocalDate;

@Entity
@Table(name = "groups_users")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class GroupRequest {

    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    @Getter
    @Setter
    @ToString
    public static class Id {

        @ManyToOne
        @JoinColumn(name = "group_id")
        private Group group;

        @ManyToOne
        @JoinColumn(name = "user_id")
        private User user;
    }

    @EmbeddedId
    private Id id;

    @CreationTimestamp(source = SourceType.VM)
    @Column(name = "request_sent")
    private LocalDate sent;

    @Column(name = "request_accepted")
    private LocalDate accepted;

    public GroupRequest(Id id) {
        this.id = id;
    }

    private Byte isAdmin;

    public boolean isAdmin() {
        return (isAdmin != null) && (isAdmin == 1);
    }

    public boolean isMember() {
        return getStatus() == RequestStatus.ACCEPTED;
    }

    public RequestStatus getStatus() {
        return sent != null && accepted != null
                ? RequestStatus.ACCEPTED
                : sent != null
                ? RequestStatus.SENT
                : RequestStatus.NOT_SENT;
    }
}