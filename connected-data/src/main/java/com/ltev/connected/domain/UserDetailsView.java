package com.ltev.connected.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Table(name = "user_data_view")
@Getter
@ToString
public class UserDetailsView {

    @Id
    private Long id;

    private String username;
    private String firstName;
    private String lastName;
    private LocalDate birthday;
}