package com.ltev.connected.domain;

import com.ltev.connected.domain.callback.UserJpaCallback;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Entity
@EntityListeners(UserJpaCallback.class)
@Table(name = "users")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 3, max = 50, message = "Must have from 3 to 50 characters.")
    private String username;

    @NotNull
    @NotEmpty
    @NotBlank
    // @Convert(converter = PasswordConverter.class)
    private String password;

    @NotNull
    private Byte enabled;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Post> posts;

    @NotNull
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private ProfileSettings profileSettings;

    public User(Long id) {
        this.id = id;
    }

    public User(Long id, String username) {
        this.id = id;
        this.username = username;
    }

    public void setProfileSettings(ProfileSettings profileSettings) {
        this.profileSettings = profileSettings;
        if (profileSettings != null && profileSettings.getUser() != this) {
            profileSettings.setUser(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return getUsername().equals(user.getUsername());
    }

    @Override
    public int hashCode() {
        return getUsername().hashCode();
    }
}