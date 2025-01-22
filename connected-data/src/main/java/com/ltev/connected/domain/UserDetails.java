package com.ltev.connected.domain;

import com.ltev.connected.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

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

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthday;

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