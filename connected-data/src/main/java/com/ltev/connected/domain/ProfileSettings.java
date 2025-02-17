package com.ltev.connected.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "user_settings", indexes = @Index(name = "user_id_idx", columnList = "id"))
@Getter
@Setter
@ToString
public class ProfileSettings {

    @Id
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id")
    private User user;

    @Version
    private Short version;

    @NotNull
    private Visibility friendsVisibility;

    @NotNull
    private Visibility groupsVisibility;

    public void setUser(User user) {
        this.user = user;
        if (user != null && user.getProfileSettings() != this) {
            user.setProfileSettings(this);
        }
    }

    @Override
    public String toString() {
        return "ProfileSettings{" +
                "id=" + id +
                ", userId=" + (user == null ? null : user.getId()) +
                ", version=" + version +
                ", friendsVisibility=" + friendsVisibility +
                ", groupsVisibility=" + groupsVisibility +
                '}';
    }
}
