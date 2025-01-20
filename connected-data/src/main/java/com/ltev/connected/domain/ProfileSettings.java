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
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "id")
    private User user;

    @Version
    private Short version;

    @NotNull
    private Visibility friendsVisibility;

    @NotNull
    private Visibility groupsVisibility;
}
