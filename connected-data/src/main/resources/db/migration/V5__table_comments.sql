create table comments (
        id bigint not null auto_increment,
        created timestamp not null,
        user_id bigint not null,
        post_id bigint not null,
        text varchar(1000) not null,
        primary key (id),
        constraint `comments_user_id_fk` foreign key (user_id) references users (id),
        constraint `comments_post_id_fk` foreign key (post_id) references posts (id)
) engine=InnoDB default charset=latin1;