package com.ltev.connected.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "user_details")
@Getter
@Setter
@ToString
public class UserDetails {

    @Id
    private Long id;

    @MapsId
    @OneToOne
    @JoinColumn(name = "id")
    private User user;

    private String firstName;
    private String lastName;

    public void setId(Long id) {
        if (user != null && user.getId() != id) {
            throw new RuntimeException("Different id values!");
        }
        this.id = id;
    }

    public void setUser(User user) {
        if (id != null && user.getId() != id) {
            throw new RuntimeException("Different id values!");
        }
        this.user = user;
    }
}