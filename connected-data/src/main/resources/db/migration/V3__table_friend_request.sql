create table friend_requests (
        id bigint not null auto_increment,
		from_user_id bigint not null,
        to_user_id bigint not null,
		sent date not null,
        accepted date,
        primary key (id),
        constraint `friend_request_from_user_fk` FOREIGN KEY (from_user_id) REFERENCES users (id),
        constraint `friend_request_to_user_fk` FOREIGN KEY (to_user_id) REFERENCES users (id)
) engine=InnoDB default charset=latin1;