create table posts (
        id bigint not null auto_increment,
        created timestamp not null,
        user_id bigint not null,
        visibility tinyint not null check (visibility between 0 and 3),
        text varchar(1000) not null,
        primary key (id),
        constraint `posts_user_id_fk` foreign key (user_id) references users (id)
) engine=InnoDB default charset=latin1;