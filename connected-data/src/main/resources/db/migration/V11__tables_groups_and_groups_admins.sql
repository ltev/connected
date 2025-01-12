create table api_groups (
        id bigint not null auto_increment,
        created timestamp not null,
        name varchar(255) not null,
        description varchar(1000) not null,
        primary key (id),
		unique key api_groups_name_idx (name)
) engine=InnoDB default charset=latin1;

create table groups_admins (
		group_id bigint not null,
        user_id bigint not null,
        primary key (group_id, user_id),
		constraint `groups_admins_group_id_fk` foreign key (group_id) references api_groups (id),
        constraint `groups_admins_user_id_fk` foreign key (user_id) references users (id)
) engine=InnoDB default charset=latin1;