package com.ltev.connected.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.ZonedDateTime;

@Entity
@Table(name = "messages")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Message {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Id {

        @NotNull
        @ManyToOne
        @JoinColumn(name = "from_user_id")
        private User fromUser;

        @NotNull
        @ManyToOne
        @JoinColumn(name = "to_user_id")
        private User toUser;

        private ZonedDateTime created;
    }

    @EmbeddedId
    private Id id;

    @NotNull
    @NotEmpty
    @Size(min = 1, max = 1000)
    private String text;

    public Message(Id id) {
        this.id = id;
    }
}
