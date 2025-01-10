create table user_details (
        id bigint not null,
        first_name varchar(255),
        last_name varchar(255),
        primary key (id),
        constraint `user_details_user_id_fk` foreign key (id) references users (id)
) engine=InnoDB default charset=latin1;