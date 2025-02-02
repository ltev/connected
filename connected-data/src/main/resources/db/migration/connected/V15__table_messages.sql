create table messages (
		from_user_id bigint not null,
        to_user_id bigint not null,
        created timestamp not null,
        text varchar(1000) not null,
        primary key (from_user_id, to_user_id, created),
        constraint messages_from_user_id foreign key (from_user_id) references users (id),
        constraint messages_to_user_id foreign key (to_user_id) references users(id)
) engine=InnoDB default charset=latin1;