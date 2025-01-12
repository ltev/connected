create table api_groups (
        id bigint not null auto_increment,
        created timestamp not null,
        name varchar(255) not null,
        description varchar(1000) not null,
        primary key (id),
		unique key api_groups_name_idx (name)
) engine=InnoDB default charset=latin1;

create table groups_users (
		group_id bigint not null,
        user_id bigint not null,
		request_sent date not null,
        request_accepted date,
        is_admin tinyint,
        primary key (group_id, user_id),
        constraint `group_requests_group_id_fk` FOREIGN KEY (group_id) REFERENCES api_groups (id),
        constraint `group_requests_user_id_fk` FOREIGN KEY (user_id) REFERENCES users (id)
) engine=InnoDB default charset=latin1;