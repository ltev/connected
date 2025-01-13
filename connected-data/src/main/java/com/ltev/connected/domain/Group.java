package com.ltev.connected.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;

import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Table(name = "api_groups")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp(source = SourceType.VM)
    private ZonedDateTime created;

    private String name;
    private String description;

    @Transient
    private List<User> admins;

    @Transient
    private List<User> members;

    public Group(Long id) {
        this.id = id;
    }
}