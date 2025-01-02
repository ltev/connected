package com.ltev.connected.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;

import java.time.ZonedDateTime;

@Entity
@Table(name = "posts")
@Getter
@Setter
public class Post {

    /**
     * ORDER should not be changed!
     */
    public enum Visibility {
        MYSELF,
        FRIENDS,
        LOGGED_USERS,
        EVERYONE;

        public String displayName() {
            return this.toString().toLowerCase();
        }

        public static Visibility ofOrdinal(byte visibility) {
            for (Visibility temp : Visibility.values()) {
                if (temp.ordinal() == visibility) {
                    return temp;
                }
            }
            throw new RuntimeException("No visibility of ordinal: " + visibility);
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp(source = SourceType.VM)
    private ZonedDateTime created;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.ORDINAL)
    private Visibility visibility;

    private String text;

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", created=" + created +
                ", user=" + (user != null ? user.getUsername() : null) +
                ", visibility=" + visibility +
                ", text='" + text + '\'' +
                '}';
    }
}
