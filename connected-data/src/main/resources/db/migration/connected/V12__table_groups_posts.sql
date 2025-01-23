create table groups_posts (
		group_id bigint not null,
        post_id bigint not null,
        primary key (group_id, post_id),
        constraint `groups_posts_group_id_fk` FOREIGN KEY (group_id) REFERENCES api_groups (id),
        constraint `groups_posts_post_id_fk` FOREIGN KEY (post_id) REFERENCES posts (id)
) engine=InnoDB default charset=latin1;