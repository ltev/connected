package com.ltev.connected.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts")
@NoArgsConstructor
@Getter
@Setter
public class Post {

    /**
     * ORDER should not be changed! Ordinals are used in for storing in DB
     */
    public enum Visibility {
        PRIVATE,
        GROUP_PRIVATE,
        FRIENDS,
        LOGGED_USERS,
        PUBLIC;

        private static int TOTAL_SIZE = 4;

        public static boolean atLeast(Visibility expected, Visibility actual) {
            return expected.ordinal() <= actual.ordinal();
        }

        public static List<Visibility> ofAtLeast(Visibility atLeast) {
            int size = TOTAL_SIZE - atLeast.ordinal();
            List<Visibility> list = new ArrayList<>(size);
            for (byte i = (byte) atLeast.ordinal(); i < TOTAL_SIZE; i++) {
                list.add(Visibility.ofOrdinal(i));
            }
            return list;
        }

        public static Visibility ofOrdinal(byte visibility) {
            for (Visibility temp : Visibility.values()) {
                if (temp.ordinal() == visibility) {
                    return temp;
                }
            }
            throw new RuntimeException("No visibility of ordinal: " + visibility);
        }

        public String displayName() {
            return this.toString().toLowerCase();
        }

        public boolean isForFriends() {
            return this.ordinal() >= FRIENDS.ordinal();
        }

        public boolean isForLoggedUsers() {
            return this.ordinal() >= LOGGED_USERS.ordinal();
        }

        public boolean isPublic() {
            return this.ordinal() == PUBLIC.ordinal();
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp(source = SourceType.VM)
    private ZonedDateTime created;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @Enumerated(EnumType.ORDINAL)
    private Visibility visibility;

    private String title;

    @NotNull
    @NotBlank
    @Size(min = 4, max = 1000)
    private String text;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private List<Comment> comments;

    private long numComments;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinTable(name = "groups_posts",
        joinColumns = @JoinColumn(name = "post_id"),
        inverseJoinColumns = @JoinColumn(name = "group_id"))
    private Group group;

    public Post(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", created=" + created +
                ", user=" + (user != null ? user.getUsername() : null) +
                ", visibility=" + visibility +
                ", title=" + title +
                ", text='" + text + '\'' +
                ", group=" + group +
                '}';
    }
}