create table user_settings (
		id bigint not null,
        version smallint not null,
        friends_visibility tinyint not null,
        groups_visibility tinyint not null,
        primary key (id),
		constraint user_settings_id_fk foreign key (id) references users (id),
        key user_id_idx (id)
) engine=InnoDB default charset=latin1;

insert into user_settings (id, version, friends_visibility, groups_visibility)
select id, 0, 3, 3
from users;