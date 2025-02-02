package com.ltev.connected.domain;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

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

        @JoinColumn(name = "from_user_id")
        private User fromUser;

        @JoinColumn(name = "to_user_id")
        private User toUser;

        private LocalDateTime created;
    }

    @EmbeddedId
    private Id id;

    @NotNull
    @NotEmpty
    @Size(min = 1, max = 1000)
    private String text;
}
