package com.ltev.connected.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;

import java.time.LocalDate;

@Entity
@Table(name = "groups_users")
@Getter
@Setter
@ToString
public class GroupRequest {

    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class Id {

        private Long groupId;
        private Long userId;
    }

    @EmbeddedId
    private Id id;

    @CreationTimestamp(source = SourceType.VM)
    @Column(name = "request_sent")
    private LocalDate sent;

    @Column(name = "request_accepted")
    private LocalDate accepted;

    private byte isAdmin;

    public boolean isAdmin() {
        return isAdmin == 1;
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
