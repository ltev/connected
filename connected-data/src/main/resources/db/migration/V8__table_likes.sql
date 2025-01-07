create table likes (
		post_id bigint not null,
        user_id bigint not null,
        value tinyint not null,
        primary key (post_id, user_id),
		constraint `likes_post_id_fk` foreign key (post_id) references posts (id),
        constraint `likes_user_id_fk` foreign key (user_id) references users (id)
) engine=InnoDB default charset=latin1;