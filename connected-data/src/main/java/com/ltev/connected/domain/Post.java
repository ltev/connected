package com.ltev.connected.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;
import org.springframework.lang.NonNull;

import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Table(name = "posts")
@NoArgsConstructor
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

        public static boolean atLeast(Visibility expected, Visibility actual) {
            return expected.ordinal() <= actual.ordinal();
        }

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

    public Post(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp(source = SourceType.VM)
    private ZonedDateTime created;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.ORDINAL)
    private Visibility visibility;

    private String title;

    @NonNull
    private String text;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private List<Comment> comments;

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", created=" + created +
                ", user=" + (user != null ? user.getUsername() : null) +
                ", visibility=" + visibility +
                ", title=" + title +
                ", text='" + text + '\'' +
                '}';
    }
}