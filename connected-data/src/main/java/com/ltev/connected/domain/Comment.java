package com.ltev.connected.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;

import java.time.ZonedDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp(source = SourceType.VM)
    private ZonedDateTime created;

    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @NotNull
    @Size(min = 1, max = 1000)
    private String text;
}