package com.ltev.connected.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "likes")
@Getter
@Setter
@ToString
public class Like {

    /**
     * ORDER should not be changed! Ordinals are used in for storing in DB
     */
    public static enum Value {
        DISLIKE,
        INDIFFERENCE,
        LIKE
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class Id {

        @ManyToOne
        @JoinColumn(name = "post_id")
        private Post post;

        @ManyToOne
        @JoinColumn(name = "user_id")
        private User user;
    }

    @EmbeddedId
    private Like.Id id;

    @Enumerated(value = EnumType.ORDINAL)
    private Like.Value value;
}