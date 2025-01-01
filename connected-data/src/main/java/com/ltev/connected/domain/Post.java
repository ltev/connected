package com.ltev.connected.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "posts")
@Getter
@Setter
@ToString
public class Post {

    /**
     * ORDER should not be changed!
     */
    public enum Visibility {
        MYSELF,
        FRIENDS,
        LOGGED_USERS,
        EVERYONE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    private User user;

    @Enumerated(EnumType.ORDINAL)
    private Visibility visibility;

    private String text;
}
